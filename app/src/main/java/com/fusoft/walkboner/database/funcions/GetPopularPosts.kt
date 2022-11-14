package com.fusoft.walkboner.database.funcions

import com.fusoft.walkboner.models.Post
import com.fusoft.walkboner.singletons.UserSingletone
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await
import java.lang.Boolean
import kotlin.Int
import kotlin.String
import kotlin.toString

object GetPopularPosts {
    private var a = 0
    suspend fun get(listener: PostsListener, limit: Int) {
        val firestore = FirebaseFirestore.getInstance()
        val posts: MutableList<Post> = ArrayList()

        var error = false

        var postsQuerySnapshot: QuerySnapshot? = null
        a = 0
        firestore.collection("posts").orderBy("likesAmount", Query.Direction.DESCENDING).get()
            .addOnSuccessListener { queryDocumentSnapshots: QuerySnapshot ->
                postsQuerySnapshot = queryDocumentSnapshots
            }.addOnFailureListener { e ->
                listener.OnError(e.message)
                error = true
            }.await()

        if (error) return

        for (document in postsQuerySnapshot?.documents!!) {
            if (limit != a) {
                val userUid = UserSingletone.getInstance().user.userUid
                val post = Post()
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
                val likesList: MutableList<String?> = java.util.ArrayList()

                // Get Likes
                var postsLikesQuerySnapshot: QuerySnapshot? = null
                firestore.collection("posts").document(document.id).collection("likes")
                    .get().addOnSuccessListener { queryDocumentSnapshots1: QuerySnapshot ->
                        postsLikesQuerySnapshot = queryDocumentSnapshots1
                    }.await()

                if (postsLikesQuerySnapshot == null || postsLikesQuerySnapshot?.isEmpty!!) return

                for (docLikes in postsLikesQuerySnapshot?.documents!!) {
                    likesList.add(docLikes.getString("userUid"))
                    if (docLikes.getString("userUid").contentEquals(userUid)) {
                        post.isUserLikedPost = true
                    }
                }

                post.postLikes = likesList
                posts.add(post)
                a++
            } else {
                listener.OnLoaded(posts)
            }
        }
    }

    interface PostsListener {
        fun OnLoaded(posts: List<Post>?)
        fun OnError(error: String?)
    }
}