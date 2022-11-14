package com.fusoft.walkboner.database.funcions.userProfile

import android.util.Log
import com.fusoft.walkboner.models.Post
import com.fusoft.walkboner.singletons.UserSingletone
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await
import java.lang.Boolean
import kotlin.String
import kotlin.let

class GetUserLikedPosts {
    private fun log(message: String) {
        Log.e("UPostsLog", "______________")
        Log.e("UPostsLog", message)
    }

    suspend fun get(listener: UserLikedPostsListener) {
        val firestore = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        val posts: MutableList<Post> = ArrayList()
        var userDocument: DocumentSnapshot? = null
        var likedPostsQuerySnapshot: QuerySnapshot? = null
        val userDocId = UserSingletone.getInstance().user.userDocumentId
        posts.clear()

        log("Getting User Liked Posts From Database...")
        firestore.collection("users").document(userDocId).get()
            .addOnSuccessListener { userDoc ->
                log("User Finded!")
                userDocument = userDoc
            }
            .addOnFailureListener {
                listener.OnError("""Nie znaleziono użytkownika:${it.message}""".trimIndent())
            }.await()

        // Check if user is null
        if (userDocument == null) {
            listener.OnError("Nie znaleziono użytkownika!")
            return
        }

        userDocument?.reference?.collection("likedPosts")
            ?.orderBy("likeAt", Query.Direction.DESCENDING)?.get()
            ?.addOnSuccessListener { queryDocumentSnapshots ->
                log("Finded Liked Posts with size " + queryDocumentSnapshots.documents.size)
                likedPostsQuerySnapshot = queryDocumentSnapshots
            }?.await()

        // Check if no liked posts
        if (likedPostsQuerySnapshot == null || likedPostsQuerySnapshot?.isEmpty == true) {
            listener.OnLoaded(null)
            return
        }

        for (likedPostsUserDocument in likedPostsQuerySnapshot?.documents!!) {
            log(
                "Getting post data for post with UID " + likedPostsUserDocument.getString(
                    "postUid"
                )
            )
            var post: Post? = null
            var postSnapshot: DocumentSnapshot? = null
            firestore.collection("posts")
                .document(likedPostsUserDocument.getString("postUid")!!).get()
                .addOnSuccessListener { documentSnapshot ->
                    log("Finded Post with UID " + documentSnapshot.getString("postUid"))
                    postSnapshot = documentSnapshot
                }
                .addOnFailureListener { e ->
                    log("Błąd 82: " + e.message)
                    listener.OnError("Nie udało się uzyskać szczegółów posta: " + e.message)
                }.await()
            post = Post()
            post.userUid = postSnapshot?.getString("userUid")
            post.postUid = postSnapshot?.getString("postUid")
            post.postDocumentUid = postSnapshot?.id
            post.postDescription =
                postSnapshot?.getString("postDescription")
            post.postImage = postSnapshot?.getString("postImage")
            post.createdAt = postSnapshot?.getString("createdAt")
            post.showsCelebrity =
                postSnapshot?.getString("showsCelebrity")
            post.isAllowComments =
                Boolean.parseBoolean(postSnapshot?.getString("allowComments"))
            post.isUserLikedPost = true
            val likesList: MutableList<String?> = java.util.ArrayList()
            var likesQueryDocumentSnapshot: QuerySnapshot? = null
            postSnapshot?.id?.let {
                firestore.collection("posts").document(it)
                    .collection("likes").get()
                    .addOnSuccessListener { queryDocumentSnapshots1: QuerySnapshot ->
                        likesQueryDocumentSnapshot = queryDocumentSnapshots1
                    }
            }?.await()

            for (docLikes in likesQueryDocumentSnapshot?.documents!!) {
                likesList.add(docLikes.getString("userUid"))
                if (docLikes.getString("userUid")
                        .contentEquals(user!!.uid)
                ) {
                    post.isUserLikedPost = true
                }
            }

            post.postLikes = likesList
            log("Post with description | " + post.postDescription + " | finded!")
            posts.add(post)
        }

        listener.OnLoaded(posts)
        log("listener invoked")
    }

    interface UserLikedPostsListener {
        fun OnLoaded(posts: List<Post>?)
        fun OnError(error: String?)
    }
}