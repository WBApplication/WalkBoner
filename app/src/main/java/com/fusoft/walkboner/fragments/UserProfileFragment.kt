/*
 * Copyright (c) 2022 - WalkBoner.
 * UserProfileFragment.kt
 */

package com.fusoft.walkboner.fragments

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.fusoft.walkboner.MainActivity
import com.fusoft.walkboner.R
import com.fusoft.walkboner.adapters.recyclerview.ProfilePostsAdapter
import com.fusoft.walkboner.database.ImageUploadListener
import com.fusoft.walkboner.database.StorageDirectory
import com.fusoft.walkboner.database.UploadImage
import com.fusoft.walkboner.database.funcions.GetPosts
import com.fusoft.walkboner.database.funcions.userProfile.GetUserLikedPosts
import com.fusoft.walkboner.database.funcions.userProfile.GetUserLikedPosts.UserLikedPostsListener
import com.fusoft.walkboner.database.funcions.userProfile.GetUserPosts
import com.fusoft.walkboner.database.funcions.userProfile.SetDescription
import com.fusoft.walkboner.database.funcions.userProfile.SetDescription.OnDescriptionChanged
import com.fusoft.walkboner.databinding.FragmentUserProfileBinding
import com.fusoft.walkboner.models.Post
import com.fusoft.walkboner.models.User
import com.fusoft.walkboner.singletons.UserSingletone
import com.fusoft.walkboner.utils.GetPathFromUri
import com.fusoft.walkboner.views.Avatar
import com.fusoft.walkboner.views.LoadingView
import com.fusoft.walkboner.views.dialogs.ChangeProfileDescriptionDialog
import com.fusoft.walkboner.views.dialogs.ChangeProfileDescriptionDialog.ProfileDescriptionDialogListener
import com.github.drjacky.imagepicker.ImagePicker
import com.github.drjacky.imagepicker.constant.ImageProvider
import com.google.android.material.appbar.AppBarLayout.OnOffsetChangedListener
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext


class UserProfileFragment : Fragment(R.layout.fragment_user_profile), CoroutineScope {
    private var job: Job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    /* ------------------------------------------ */

    var binding: FragmentUserProfileBinding? = null
    var loading: LoadingView? = null

    private var currentUser: User? = null
    private var imageStoragePath = ""
    private var firestore: FirebaseFirestore? = null
    private var likedPostsGetter: GetUserLikedPosts? = null
    private var userPostsGetter: GetUserPosts? = null
    private var adapter: ProfilePostsAdapter? = null

    override fun onDestroy() {
        firestore = null
        likedPostsGetter = null
        userPostsGetter = null
        adapter = null
        binding = null
        loading = null
        super.onDestroy()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentUserProfileBinding.bind(view)
        loading = (activity as MainActivity).getLoading()

        initView()
        setup()
    }

    private fun initView() {
        likedPostsGetter = GetUserLikedPosts()
        userPostsGetter = GetUserPosts()

        changeProfileDataLoadingState(false)

        currentUser = UserSingletone.getInstance().user
        binding?.userNameText?.text = currentUser?.userName
        binding?.readMoreDescriptionText?.text = currentUser?.userDescription

        if (currentUser?.isUserAdmin == true) {
            binding?.image?.setAvatarOwnerPrivileges(Avatar.ADMIN)
        } else if (currentUser?.isUserModerator == true) {
            binding?.image?.setAvatarOwnerPrivileges(Avatar.MODERATOR)
        } else {
            binding?.image?.setAvatarOwnerPrivileges(Avatar.USER)
        }

        if (!currentUser?.userAvatar!!.contentEquals("default")) {
            binding?.image?.setImageFromUrl(currentUser?.userAvatar)
        }
    }

    private fun setup() {
        val likedPostsTab = binding?.profileTablayout?.newTab()?.setText("Polubione Posty")?.setId(
            LIKED_POSTS
        )
        val yourPostsTab =
            binding?.profileTablayout?.newTab()?.setText("Twoje Posty")?.setId(YOUR_POSTS)
        val layoutManager = LinearLayoutManager(activity)
        binding?.profileRecyclerView?.layoutManager = layoutManager

        if (yourPostsTab != null) binding?.profileTablayout?.addTab(yourPostsTab)
        if (likedPostsTab != null) binding?.profileTablayout?.addTab(likedPostsTab)

        launch {
            changeContent(YOUR_POSTS)
        }

        binding?.profileTablayout?.addOnTabSelectedListener(object :
            com.google.android.material.tabs.TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: com.google.android.material.tabs.TabLayout.Tab?) {
                tab?.id?.let { launch { changeContent(it) } }
            }

            override fun onTabUnselected(tab: com.google.android.material.tabs.TabLayout.Tab?) {}
            override fun onTabReselected(tab: com.google.android.material.tabs.TabLayout.Tab?) {}
        })

        var isShow = true
        var scrollRange = -1
        binding?.appBarLayout?.addOnOffsetChangedListener(OnOffsetChangedListener { appBarLayout, verticalOffset ->
            if (scrollRange == -1) {
                scrollRange = appBarLayout.totalScrollRange
            }

            if (scrollRange + verticalOffset == 0) {
                binding?.collapsingToolbarLayout?.title = getString(R.string.app_name)
                isShow = true
            } else {
                binding?.collapsingToolbarLayout?.title = " "
                isShow = false
            }
        })

        binding?.image?.setOnClickListener { v: View? -> imageChooser() }
        binding?.editDescriptionButton?.setOnClickListener { v: View? ->
            ChangeProfileDescriptionDialog.Show(
                activity,
                currentUser?.userDescription,
                object : ProfileDescriptionDialogListener {
                    override fun OnChange(newDescription: String) {
                        loading?.show()
                        SetDescription.Set(
                            currentUser?.userDocumentId,
                            newDescription,
                            object : OnDescriptionChanged {
                                override fun OnChanged() {
                                    loading?.hide()
                                    binding?.readMoreDescriptionText?.text = newDescription

                                    currentUser?.userDescription = newDescription
                                    UserSingletone.getInstance().user = currentUser
                                }

                                override fun OnError(reason: String) {
                                    loading?.hide()
                                    Toast.makeText(
                                        activity,
                                        reason,
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            })
                    }

                    override fun OnDismiss() {}
                })
        }
    }

    private fun imageChooser() {
        activityResultLauncher.launch(
            ImagePicker.with(activity as MainActivity)
                .crop(1f, 1f)
                .provider(ImageProvider.GALLERY)
                .createIntent()
        )
    }

    var activityResultLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult<Intent, ActivityResult>(
            ActivityResultContracts.StartActivityForResult(),
            ActivityResultCallback { result: ActivityResult ->
                if (result.resultCode == RESULT_OK) {
                    loading?.show()
                    imageStoragePath =
                        GetPathFromUri.getPath(activity, result.data!!.data)
                    binding?.image?.setImageFromURI(result.data!!.data)
                    UploadImage(
                        activity,
                        StorageDirectory.AVATARS_PATH,
                        result.data!!.data,
                        object : ImageUploadListener {
                            override fun OnImageUploaded(imageUrl: String) {
                                firestore = FirebaseFirestore.getInstance()
                                currentUser?.userDocumentId?.let {
                                    firestore!!.collection("users")
                                        .document(it).get()
                                        .addOnSuccessListener { queryDocumentSnapshots ->
                                            val map = HashMap<String, Any>()
                                            map["avatar"] = imageUrl
                                            queryDocumentSnapshots.reference.update(map)
                                                .addOnSuccessListener {
                                                    Toast.makeText(
                                                        activity,
                                                        "Zmieniono!",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                    loading?.hide()
                                                }
                                                .addOnFailureListener { e ->
                                                    Toast.makeText(
                                                        activity,
                                                        "Błąd:\n" + e.message,
                                                        Toast.LENGTH_LONG
                                                    ).show()
                                                    loading?.hide()
                                                }
                                        }.addOnFailureListener { e ->
                                            Toast.makeText(
                                                activity,
                                                "Błąd:\n" + e.message,
                                                Toast.LENGTH_LONG
                                            ).show()
                                            loading?.hide()
                                        }
                                }
                            }

                            override fun Progress(value: Int) {}
                            override fun OnError(reason: String) {
                                Toast.makeText(
                                    activity,
                                    "Błąd:\n$reason",
                                    Toast.LENGTH_LONG
                                ).show()
                                loading?.hide()
                            }
                        })
                }
            })

    private suspend fun changeContent(id: Int) {
        changeContentLoadingState(true)
        if (id == LIKED_POSTS) {
            likedPostsGetter?.get(object :
                UserLikedPostsListener {
                override fun OnLoaded(posts: List<Post>?) {
                    if (binding?.profileRecyclerView?.adapter != null) {
                        binding?.profileRecyclerView?.adapter = null
                    }

                    changeContentLoadingState(false)
                    if (posts != null) {
                        adapter =
                            activity?.let { ProfilePostsAdapter(it, posts as ArrayList<Post>) }
                        adapter!!.setHeartClickListener(object :
                            ProfilePostsAdapter.HeartClickListener {
                            override fun onHeartClick(position: Int) {
                                adapter!!.removeFromList(position)
                            }
                        })
                        binding?.profileRecyclerView?.adapter = adapter
                    } else {
                        Toast.makeText(
                            activity,
                            "Brak polubionych postów!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun OnError(error: String?) {
                    Toast.makeText(
                        activity,
                        error,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        } else {
            userPostsGetter?.get(object : GetPosts.PostsListener {
                override fun OnLoaded(posts: MutableList<Post>?) {
                    if (binding?.profileRecyclerView?.adapter != null) {
                        binding?.profileRecyclerView?.adapter = null
                    }
                    changeContentLoadingState(false)
                    adapter = activity?.let { ProfilePostsAdapter(it, posts as ArrayList<Post>) }
                    binding?.profileRecyclerView?.setAdapter(adapter)
                }

                override fun OnError(error: String?) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                }
            })
        }
    }

    private fun changeProfileDataLoadingState(on: Boolean) {
        if (on) {
            binding?.profileDetailsLoadingLinear?.visibility = View.VISIBLE
            binding?.profileDetailsLinear?.visibility = View.GONE
        } else {
            binding?.profileDetailsLoadingLinear?.visibility = View.GONE
            binding?.profileDetailsLinear?.visibility = View.VISIBLE
        }
    }

    private fun changeContentLoadingState(on: Boolean) {
        if (on) {
            binding?.contentLinear?.visibility = View.GONE
            binding?.loadingContentLinear?.visibility = View.VISIBLE
        } else {
            binding?.contentLinear?.visibility = View.VISIBLE
            binding?.loadingContentLinear?.visibility = View.GONE
        }
    }

    companion object {
        private const val LIKED_POSTS = 0
        private const val YOUR_POSTS = 1
    }
}