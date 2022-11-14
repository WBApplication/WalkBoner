package com.fusoft.walkboner.adapters.recyclerview

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.fusoft.walkboner.FullPhotoViewerActivity
import com.fusoft.walkboner.MainActivity
import com.fusoft.walkboner.ProfileActivity
import com.fusoft.walkboner.R
import com.fusoft.walkboner.models.Post
import com.fusoft.walkboner.models.User
import com.fusoft.walkboner.settings.Settings
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class PopularPostsAdapter(private val context: Context, data: List<Post>, settings: Settings) :
    RecyclerView.Adapter<PopularPostsAdapter.ViewHolder>() {
    private val mData: List<Post>
    private val mInflater: LayoutInflater
    private val mClickListener: ItemClickListener? = null
    private val settings: Settings

    // data is passed into the constructor
    init {
        mInflater = LayoutInflater.from(context)
        mData = data
        this.settings = settings
    }

    // inflates the row layout from xml when needed
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.item_popular_post, parent, false)
        return ViewHolder(view)
    }

    // binds the data to the TextView in each row
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = mData[position]
        val firestore = FirebaseFirestore.getInstance()

        if (post.isUserLikedPost) {
            holder.likeImage.setImageResource(R.drawable.ic_favorite_filled_wb)
        } else {
            holder.likeImage.setImageResource(R.drawable.ic_favorite_wb)
        }

        firestore.collection("users").whereEqualTo("userUid", post.userUid).get()
            .addOnSuccessListener { queryDocumentSnapshots: QuerySnapshot ->
                for (doc in queryDocumentSnapshots.documents) {
                    val userData = User()
                    userData.userName = doc["username"].toString()
                    userData.userAvatar = doc["avatar"].toString()
                    userData.isUserVerified =
                        java.lang.Boolean.parseBoolean(doc["isVerified"].toString())
                    userData.isUserModerator =
                        java.lang.Boolean.parseBoolean(doc["isMod"].toString())
                    userData.isUserAdmin = java.lang.Boolean.parseBoolean(doc["isAdmin"].toString())
                    userData.isUserBanned =
                        java.lang.Boolean.parseBoolean(doc["isBanned"].toString())
                    holder.postImage.setOnClickListener { view: View? ->
                        val intent = Intent(
                            context, FullPhotoViewerActivity::class.java
                        )
                        intent.putExtra("imageUrl", post.postImage)
                        intent.putExtra("type", "postImage")
                        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                            (context as MainActivity),
                            (holder.postImage as View),
                            "postImage"
                        )
                        context.startActivity(intent, options.toBundle())
                    }
                    val glideListener: RequestListener<Drawable?> =
                        object : RequestListener<Drawable?> {
                            override fun onLoadFailed(
                                e: GlideException?,
                                model: Any,
                                target: Target<Drawable?>,
                                isFirstResource: Boolean
                            ): Boolean {
                                return false
                            }

                            override fun onResourceReady(
                                resource: Drawable?,
                                model: Any,
                                target: Target<Drawable?>,
                                dataSource: DataSource,
                                isFirstResource: Boolean
                            ): Boolean {
                                holder.postImage.visibility = View.VISIBLE
                                holder.loadingBar.visibility = View.GONE
                                holder.likes_top_linear.visibility = View.VISIBLE
                                holder.likes_amount_text.text = post.postLikes.size.toString()
                                return false
                            }
                        }
                    if (settings.isPrivateMode) { //If PrivateMode -> load private image
                        Glide.with(context).load(
                            AppCompatResources.getDrawable(
                                context, R.drawable.walkboner_private
                            )
                        ).into(holder.postImage)
                        holder.postImage.visibility = View.VISIBLE
                        holder.loadingBar.visibility = View.GONE
                        holder.likes_top_linear.visibility = View.VISIBLE
                        holder.likes_amount_text.text = post.postLikes.size.toString()
                    } else {
                        Glide.with(context).load(post.postImage).addListener(glideListener)
                            .into(holder.postImage)
                    }
                }
            }
    }

    // total number of rows
    override fun getItemCount(): Int {
        return mData.size
    }

    private fun parseLikes(amount: Int): String {
        return if (amount == 0) {
            "$amount polubień"
        } else if (amount == 1) {
            "$amount polubienie"
        } else if (amount <= 4 && amount >= 2) {
            "$amount polubienia"
        } else {
            "$amount polubień"
        }
    }

    private fun openProfile(uid: String) {
        val mainActivity = context as MainActivity
        val intent = Intent(mainActivity, ProfileActivity::class.java)
        intent.putExtra("userUid", uid)
        mainActivity.startActivity(intent)
        mainActivity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    // stores and recycles views as they are scrolled off screen
    inner class ViewHolder internal constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var postImage: ImageView
        var likeImage: ImageView
        var loadingBar: CircularProgressIndicator
        var likes_top_linear: LinearLayout
        var likes_amount_text: MaterialTextView

        init {
            postImage = itemView.findViewById(R.id.image)
            likeImage = itemView.findViewById(R.id.like_image)
            postImage.visibility = View.INVISIBLE
            loadingBar = itemView.findViewById(R.id.loading_progress_bar)
            likes_top_linear = itemView.findViewById(R.id.likes_top_linear)
            likes_amount_text = itemView.findViewById(R.id.likes_amount_text)
        }
    }

    // convenience method for getting data at click position
    fun getPost(id: Int): Post {
        return mData[id]
    }
}