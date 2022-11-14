package com.fusoft.walkboner

import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import com.fusoft.walkboner.views.LoadingView
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.firebase.firestore.FirebaseFirestore
import java.lang.Boolean
import android.os.Bundle
import com.fusoft.walkboner.R
import com.fusoft.walkboner.auth.UserInfoListener
import android.animation.LayoutTransition
import com.fusoft.walkboner.database.DeleteImage
import com.fusoft.walkboner.database.DeleteImage.DeleteTask
import com.fusoft.walkboner.database.UploadImage
import com.fusoft.walkboner.database.StorageDirectory
import com.fusoft.walkboner.database.ImageUploadListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.OnFailureListener
import com.fusoft.walkboner.views.dialogs.ErrorDialog
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.*
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.fusoft.walkboner.adapters.spinner.InfluencersNicksAdapter
import com.fusoft.walkboner.auth.Authentication
import com.fusoft.walkboner.databinding.ActivityCreatePostBinding
import com.fusoft.walkboner.models.User
import com.fusoft.walkboner.singletons.UserSingletone
import com.squareup.picasso.Picasso
import com.fusoft.walkboner.utils.UidGenerator
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.lang.Exception
import java.sql.Timestamp
import java.util.HashMap
import kotlin.coroutines.CoroutineContext

class CreatePostActivity : AppCompatActivity(), CoroutineScope {
    private var job: Job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    /* ------------------------------------------ */

    lateinit var binding: ActivityCreatePostBinding

    private var imageStoragePath: Uri? = null
    private var imageUrl = ""
    private var allowComments = true

    var firestore: FirebaseFirestore? = null
    private var authentication: Authentication? = null
    private var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreatePostBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firestore = FirebaseFirestore.getInstance()

        user = UserSingletone.getInstance().user
        binding.addButton.isEnabled = true

        initView()
        setup()
    }

    private fun initView() {
        binding.mainLinear.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)

        launch { loadInfluencersDataToSpinner() }
    }

    private fun setup() {
        binding.image.setOnClickListener { v: View? -> imageChooser() }
        binding.addButton.setOnClickListener { v: View? ->
            if (imageStoragePath != null) {
                if (!imageUrl.isEmpty()) {
                    DeleteImage.Delete(this@CreatePostActivity, imageUrl, object : DeleteTask {
                        override fun OnDeleted() {}
                        override fun OnError(reason: String) {}
                    })
                }

                binding.uploadingImageLinear.visibility = View.VISIBLE
                UploadImage(
                    this@CreatePostActivity,
                    StorageDirectory.POSTS_PATH,
                    imageStoragePath,
                    object : ImageUploadListener {
                        override fun OnImageUploaded(imageUrlString: String) {
                            imageUrl = imageUrlString
                            firestore!!.collection("posts").add(defaultMap())
                                .addOnSuccessListener { documentReference: DocumentReference ->
                                    val map = HashMap<String, Any>()
                                    map["documentUid"] = documentReference.id
                                    firestore!!.collection("posts").document(documentReference.id)
                                        .update(map)
                                    finish()
                                }.addOnFailureListener { e: Exception ->
                                ErrorDialog().SimpleErrorDialog(
                                    this@CreatePostActivity,
                                    e.message
                                )
                            }
                        }

                        override fun Progress(value: Int) {
                            binding.imageUploadingProgressBar.progress = value
                        }

                        override fun OnError(reason: String) {
                            Toast.makeText(this@CreatePostActivity, reason, Toast.LENGTH_SHORT)
                                .show()
                        }
                    })
            } else {
                Toast.makeText(
                    this@CreatePostActivity,
                    "Wybierz Zdjęcie, które chcesz załączyć do postu",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.allowCommentsSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            allowComments = isChecked
        }
    }

    private suspend fun loadInfluencersDataToSpinner() {
        var influencers: QuerySnapshot? = null
        var influencersData: ArrayList<HashMap<String, String>>? = arrayListOf()
        var spinnerAdapter: InfluencersNicksAdapter? = null

        firestore?.collection("influencers")?.get()?.addOnSuccessListener { querySnapshot ->
            influencers = querySnapshot
        }?.await()

        if (influencers == null || influencers?.isEmpty == true) {
            Toast.makeText(this@CreatePostActivity, "Nie udało się załatować listy influencerek!", Toast.LENGTH_SHORT).show()
            return
        }

        for (influencer in influencers?.documents!!) {
            if (!Boolean.parseBoolean(influencer?.get("isHidden").toString())) {
                val influ = HashMap<String, String>()
                influ.put("nickname", influencer?.getString("influencerNickName").toString())
                influ.put("uid", influencer?.id.toString())
                influencersData?.add(influ)
            }
        }

        // sort list a-z
        influencersData?.sortBy { it.get("nickname").toString() }

        var non = HashMap<String, String>()
        non.put("nickname", "Nikt")
        non.put("uid", "null")

        // FIXME wtf
        influencersData?.reverse()
        influencersData?.add(non)
        influencersData?.reverse()

        spinnerAdapter = InfluencersNicksAdapter(this@CreatePostActivity, influencersData)
        binding.celebritiesSpinner.adapter = spinnerAdapter
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

    var activityResultLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult<Intent, ActivityResult>(
            ActivityResultContracts.StartActivityForResult(),
            ActivityResultCallback { result: ActivityResult ->
                if (result.resultCode == RESULT_OK) {
                    imageStoragePath = result.data!!.data
                    Picasso.get().load(imageStoragePath).into(binding.image)
                }
            })

    private fun defaultMap(): HashMap<String, Any> {
        val timestamp = Timestamp(System.currentTimeMillis())
        val map = HashMap<String, Any>()
        val hashMap: HashMap<String, String> = binding.celebritiesSpinner.getItemAtPosition(binding.celebritiesSpinner.selectedItemPosition) as HashMap<String, String>
        map["userUid"] = user!!.userUid
        map["postUid"] = UidGenerator.Generate()
        map["postDescription"] = binding.descriptionEdt.text.toString()
        map["postImage"] = imageUrl
        map["documentUid"] = ""
        map["createdAt"] = timestamp.time.toString()
        map["showsCelebrity"] = hashMap["uid"].toString()
        map["allowComments"] = allowComments.toString()
        return map
    }
}