package com.fusoft.walkboner.database.funcions.userProfile

import android.util.Log
import com.fusoft.walkboner.database.funcions.GetPosts
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.fusoft.walkboner.models.Post
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.DocumentSnapshot
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import java.lang.Boolean
import java.util.ArrayList

class GetUserPosts {
    suspend fun get(listener: GetPosts.PostsListener) {
        val firestore = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        val posts: MutableList<Post> = ArrayList()
        var post: Post
        var finalQueryDocumentSnapshot: QuerySnapshot? = null
        firestore.collection("posts").orderBy("createdAt", Query.Direction.DESCENDING)
            .whereEqualTo("userUid", user!!.uid).get()
            .addOnSuccessListener { queryDocumentSnapshots: QuerySnapshot ->
                finalQueryDocumentSnapshot = queryDocumentSnapshots
            }.addOnFailureListener { e ->
            listener.OnError(e.message)
            Log.e("UserPosts Error", e.message!!)
        }.await()

        for (document in finalQueryDocumentSnapshot!!) {
            post = Post()
            post.userUid = document["userUid"].toString()
            post.postUid = document["postUid"].toString()
            post.postDescription = document["postDescription"].toString()
            post.postImage = document["postImage"].toString()
            post.createdAt = document["createdAt"].toString()
            post.showsCelebrity = document["showsCelebrity"].toString()
            post.isAllowComments =
                Boolean.parseBoolean(document["allowComments"].toString())
            post.isUserLikedPost = false
            post.postDocumentUid = document.id

            val likesList: MutableList<String?> = ArrayList()
            // Get Likes
            firestore.collection("posts").document(document.id).collection("likes").get()
                .addOnSuccessListener { queryDocumentSnapshots1: QuerySnapshot ->
                    for (docLikes in queryDocumentSnapshots1.documents) {
                        likesList.add(docLikes.getString("userUid"))
                        if (docLikes.getString("userUid").contentEquals(user.uid)) {
                            post.isUserLikedPost = true
                        }
                    }
                }.await()
            post.postLikes = likesList
            posts.add(post)
        }

        listener.OnLoaded(posts)
    }

    interface PostsListener {
        fun OnLoaded(posts: List<Post?>?)
        fun OnError(error: String?)
    }
}