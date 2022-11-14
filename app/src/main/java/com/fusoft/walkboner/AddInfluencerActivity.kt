/*
 * Copyright (c) 2022 - WalkBoner.
 * AddInfluencerActivity.kt
 * created at: 06.11.2022, 20:16
 * last modified: 06.11.2022, 21:48
 */

package com.fusoft.walkboner

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.fusoft.walkboner.adapters.spinner.InfluencersNicksAdapter
import com.fusoft.walkboner.database.DeleteImage
import com.fusoft.walkboner.database.DeleteImage.DeleteTask
import com.fusoft.walkboner.database.ImageUploadListener
import com.fusoft.walkboner.database.StorageDirectory
import com.fusoft.walkboner.database.UploadImage
import com.fusoft.walkboner.databinding.ActivityAddInfluencerBinding
import com.fusoft.walkboner.utils.UidGenerator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.tasks.await
import java.sql.Timestamp
import com.fusoft.walkboner.R.string as AppText

class AddInfluencerActivity : AppCompatActivity() {

    lateinit var binding: ActivityAddInfluencerBinding
    var database: FirebaseFirestore? = null
    var auth: FirebaseAuth? = null

    var uploadedImageUrl = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddInfluencerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
        setup()
    }

    override fun onDestroy() {
        database = null
        auth = null

        super.onDestroy()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        // If image was already uploaded to database, delete.
        if (uploadedImageUrl.isNotEmpty()) {
            binding.loadingView.show()
            DeleteImage.Delete(
                this@AddInfluencerActivity,
                uploadedImageUrl,
                object : DeleteTask {
                    override fun OnDeleted() {
                        binding.loadingView.hide()
                        finish()
                    }

                    override fun OnError(reason: String?) {
                        finish()
                    }
                })
        } else {
            super.onBackPressed()
        }
    }

    private fun init() {
        database = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
    }

    private fun setup() {
        binding.pickFromGalleryText.setOnClickListener {
            imageChooser()
        }

        binding.doneButton.setOnClickListener {
            if (validateInputs()) {
                binding.loadingView.show()
                sendInfluencerToModerators()
            }
        }
    }

    private fun sendInfluencerToModerators() {
        val timestamp = Timestamp(System.currentTimeMillis())

        val user = auth?.currentUser

        val map = HashMap<String, Any>()
        map["influencerUid"] = UidGenerator.Generate()
        map["influencerAddedBy"] = user!!.uid
        if (binding.firstNameEdittext.text.toString().isEmpty()) {
            map["influencerFirstName"] = ""
        } else {
            map["influencerFirstName"] = binding.firstNameEdittext.text.toString().trim()
        }
        if (binding.lastNameEdittext.text.toString().isEmpty()) {
            map["influencerLastName"] = ""
        } else {
            map["influencerLastName"] = binding.lastNameEdittext.text.toString().trim()
        }
        map["influencerNickName"] = binding.nickNameEdittext.text.toString().trim()
        map["influencerDescription"] = ""
        map["influencerAvatar"] = uploadedImageUrl
        map["influencerInstagramLink"] = binding.instagramUrlEdittext.text.toString().trim()
        map["influencerYouTubeLink"] = binding.youtubeUrlEdittext.text.toString().trim()
        map["influencerTikTokLink"] = binding.tiktokUrlEdittext.text.toString().trim()
        map["influencerModeratorUid"] = ""
        map["isVerified"] = "false"
        map["isPremium"] = "false"
        map["isHidden"] = "false"
        map["hasInstagram"] = !binding.instagramUrlEdittext.text.toString().isEmpty()
        map["hasYouTube"] = !binding.youtubeUrlEdittext.text.toString().isEmpty()
        map["hasTikTok"] = !binding.tiktokUrlEdittext.text.toString().isEmpty()
        map["isMaintained"] = "false"
        map["isUserFollowing"] = "true"
        map["influencerAddedAt"] = timestamp.time
        map["maintainedTo"] = timestamp.time
        map["viewsCount"] = 0

        database
            ?.collection("moderation")
            ?.document("influencers")
            ?.collection("toModerate")
            ?.add(map)
            ?.addOnSuccessListener {
                binding.loadingView.hide()
                Toast.makeText(
                    this@AddInfluencerActivity,
                    getString(AppText.influencer_sended_successfuly),
                    Toast.LENGTH_LONG
                ).show()
                finish()
            }?.addOnFailureListener { e: Exception? ->
                Toast.makeText(
                    this@AddInfluencerActivity,
                    getString(AppText.try_again) + e?.message,
                    Toast.LENGTH_LONG
                )
            }
    }

    private fun validateInputs(): Boolean {
        val nickName = binding.nickNameEdittext.text.toString()

        if (nickName.isEmpty()) {
            binding.nickNameEdittext.error = getString(AppText.field_cant_be_empty)
            return false
        } else if (nickName.length >= 16) {
            binding.nickNameEdittext.error = getString(AppText.too_many_chars)
            return false
        } else {
            binding.nickNameEdittext.error = null
            return true
        }
    }

    private fun imageChooser() {
        val i = Intent()
        i.type = "image/*"
        i.action = Intent.ACTION_GET_CONTENT

        activityResultLauncher.launch(i)
    }

    /*
     * FIXME: Wait For Fix from Google
     * Because of Android Studio Bug
     * we need to add this extension to dismiss
     * registerForActivityResult unresolved reference
     * https://stackoverflow.com/a/74139700/13680873
     */
    fun <I, O> Activity.registerForActivityResult(
        contract: ActivityResultContract<I, O>,
        callback: ActivityResultCallback<O>
    ) = (this as ComponentActivity).registerForActivityResult(contract, callback)
    /* This Function does nothing */

    var activityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val filePath = result.data!!.data
            Glide.with(this@AddInfluencerActivity).load(filePath).into(binding.image)
            binding.pickFromGalleryText.visibility = View.GONE
            binding.image.visibility = View.VISIBLE
            if (!uploadedImageUrl.isEmpty()) {
                DeleteImage.Delete(
                    this@AddInfluencerActivity,
                    uploadedImageUrl,
                    object : DeleteTask {
                        override fun OnDeleted() {
                            Toast.makeText(
                                this@AddInfluencerActivity,
                                "Poprzednie zdjęcie zostało usunięte z bazy.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        override fun OnError(reason: String) {}
                    })
                uploadedImageUrl = ""
            }
            binding.uploadingImageLinear.visibility = View.VISIBLE
            UploadImage(
                this@AddInfluencerActivity,
                StorageDirectory.INFLUENCER_PATH,
                filePath,
                object : ImageUploadListener {
                    override fun OnImageUploaded(imageUrl: String) {
                        uploadedImageUrl = imageUrl
                        binding.uploadingImageLinear.visibility = View.GONE
                    }

                    override fun Progress(value: Int) {
                        binding.uploadingProgressBar.progress = value
                    }

                    override fun OnError(reason: String) {
                        binding.uploadingProgressBar.visibility = View.GONE
                        binding.uploadingStatusText.text = reason
                    }
                }
            )
        }
    }
}