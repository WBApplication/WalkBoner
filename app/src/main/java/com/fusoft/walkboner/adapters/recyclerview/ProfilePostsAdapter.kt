package com.fusoft.walkboner.adapters.recyclerview

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.ProgressBar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.fusoft.walkboner.FullPhotoViewerActivity
import com.fusoft.walkboner.MainActivity
import com.fusoft.walkboner.ProfileActivity
import com.fusoft.walkboner.R
import com.fusoft.walkboner.auth.Authentication
import com.fusoft.walkboner.database.funcions.LikePost
import com.fusoft.walkboner.models.Post
import com.fusoft.walkboner.models.User
import com.fusoft.walkboner.views.Avatar
import com.fusoft.walkboner.views.bottomsheets.PostCommentsBSD
import com.fusoft.walkboner.views.dialogs.ReportDialog
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.yy.mobile.rollingtextview.CharOrder
import com.yy.mobile.rollingtextview.RollingTextView
import com.yy.mobile.rollingtextview.strategy.Direction
import com.yy.mobile.rollingtextview.strategy.Strategy.CarryBitAnimation
import de.dlyt.yanndroid.oneui.utils.OnSingleClickListener
import kr.co.prnd.readmore.ReadMoreTextView

class ProfilePostsAdapter(private val context: Context, data: ArrayList<Post>) :
    RecyclerView.Adapter<ProfilePostsAdapter.ViewHolder>() {
    private val mData: ArrayList<Post>
    private val mInflater: LayoutInflater
    private var mClickListener: ItemClickListener? = null
    private var mHeartListener: HeartClickListener? = null
    private var prevLikesCount = 0

    // data is passed into the constructor
    init {
        mInflater = LayoutInflater.from(context)
        mData = data
    }

    // inflates the row layout from xml when needed
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.item_post, parent, false)
        return ViewHolder(view)
    }

    // binds the data to the TextView in each row
    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val post = mData[position]
        val firestore = FirebaseFirestore.getInstance()
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
                    holder.personNickText.text = userData.userName
                    holder.personMentionNickText.text = "@" + userData.userName
                    holder.postDescriptionText.text = post.postDescription
                    //holder.postLikeText.setText(post.getPostLikes().size());
                    holder.personAvatarImage.setOnClickListener { view: View? -> openProfile(post.userUid) }
                    holder.personNickText.setOnClickListener { view: View? -> openProfile(post.userUid) }
                    holder.personMentionNickText.setOnClickListener { view: View? ->
                        openProfile(
                            post.userUid
                        )
                    }
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
                    holder.likesCounterText.charStrategy = CarryBitAnimation(Direction.SCROLL_DOWN)
                    holder.likesCounterText.addCharOrder(CharOrder.Number)
                    holder.likesCounterText.animationInterpolator = OvershootInterpolator()
                    Glide.with(context).load(post.postImage)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .addListener(object : RequestListener<Drawable?> {
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
                                val radius = 20f
                                // ViewGroup you want to start blur from. Choose root as close to BlurView in hierarchy as possible.
                                holder.postImage.visibility = View.VISIBLE
                                holder.loadingBar.visibility = View.GONE
                                val rootView = holder.imageHolder as ViewGroup
                                return false
                            }
                        }).into(holder.postImage)
                    if (userData.isUserBanned) {
                        holder.personAvatarImage.setAvatarOwnerPrivileges(Avatar.BANNED)
                    } else if (userData.isUserAdmin) {
                        holder.personAvatarImage.setAvatarOwnerPrivileges(Avatar.ADMIN)
                    } else if (userData.isUserModerator) {
                        holder.personAvatarImage.setAvatarOwnerPrivileges(Avatar.MODERATOR)
                    } else {
                        holder.personAvatarImage.setAvatarOwnerPrivileges(Avatar.USER)
                    }
                    if (!userData.userAvatar.contentEquals("default")) {
                        holder.personAvatarImage.setImageFromUrl(userData.userAvatar)
                    }
                    val popupMenu = PopupMenu(context, holder.moreButton)
                    popupMenu.inflate(R.menu.post_popup_menu)
                    popupMenu.setOnMenuItemClickListener { menuItem ->
                        if (menuItem.title == "Zgłoś") {
                            ReportDialog.ReportPostDialog(context, post.postUid)
                        }
                        true
                    }
                    val authentication = Authentication(null)
                    if (post.userUid.contentEquals(authentication.currentUserUid)) {
                        popupMenu.menu.findItem(R.id.delete_popup_button).isVisible = true
                    }
                    holder.moreButton.setOnClickListener { view: View? -> popupMenu.show() }
                    holder.commentsButton.setOnClickListener { v: View? ->
                        PostCommentsBSD.show(
                            post.postDocumentUid,
                            context
                        )
                    }
                    LikePost().LikeWatcher(post.postDocumentUid) { userLiked: Boolean, likesAmount: Int ->
                        post.isUserLikedPost = userLiked
                        if (userLiked) {
                            holder.postLikeButton.setImageDrawable(context.getDrawable(de.dlyt.yanndroid.oneui.R.drawable.ic_oui_like_on))
                        } else {
                            holder.postLikeButton.setImageDrawable(context.getDrawable(de.dlyt.yanndroid.oneui.R.drawable.ic_oui_like_off))
                        }
                        if (likesAmount > prevLikesCount) {
                            holder.likesCounterText.charStrategy =
                                CarryBitAnimation(Direction.SCROLL_DOWN)
                        } else {
                            holder.likesCounterText.charStrategy =
                                CarryBitAnimation(Direction.SCROLL_UP)
                        }
                        prevLikesCount = likesAmount
                        holder.postLikeButton.isEnabled = true
                        holder.likesCounterText.animationDuration = 200
                        holder.likesCounterText.setText(likesAmount.toString(), true)
                        holder.postLikeText.text = parseLikes(likesAmount)
                    }

                    // TODO: Dodawaj polubione posty do danyh użytkownika w bazie danych
                    holder.postLikeButton.setOnClickListener(object : OnSingleClickListener() {
                        override fun onSingleClick(view: View) {
                            holder.postLikeButton.isEnabled = false
                            if (mHeartListener != null) {
                                mHeartListener!!.onHeartClick(position)
                            }
                            if (post.isUserLikedPost) {
                                LikePost.UnLike(post.postDocumentUid)
                            } else {
                                LikePost.Like(post.postDocumentUid)
                            }
                        }
                    })
                }
            }
    }

    // total number of rows
    override fun getItemCount(): Int {
        return mData.size
    }

    private fun parseLikes(amount: Int): String {
        return if (amount == 0) {
            " polubień"
        } else if (amount == 1) {
            " polubienie"
        } else if (amount <= 4 && amount >= 2) {
            " polubienia"
        } else {
            " polubień"
        }
    }

    private fun openProfile(uid: String) {
        val intent = Intent(context, ProfileActivity::class.java)
        intent.putExtra("userUid", uid)
        context.startActivity(intent)
    }

    // stores and recycles views as they are scrolled off screen
    inner class ViewHolder internal constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var personAvatarImage: Avatar
        var postImage: ImageView
        var postLikeButton: ImageView
        var moreButton: ImageView
        var likesCounterText: RollingTextView
        var personNickText: MaterialTextView
        var personMentionNickText: MaterialTextView
        var postLikeText: MaterialTextView
        var postDescriptionText: ReadMoreTextView
        var imageHolder: ConstraintLayout
        var loadingBar: ProgressBar
        var commentsButton: ImageView

        init {
            postImage = itemView.findViewById(R.id.image)
            moreButton = itemView.findViewById(R.id.more_button)
            postImage.visibility = View.INVISIBLE
            personAvatarImage = itemView.findViewById(R.id.person_avatar_image)
            postLikeButton = itemView.findViewById(R.id.post_like_button)
            personNickText = itemView.findViewById(R.id.person_nick_text)
            personMentionNickText = itemView.findViewById(R.id.person_mention_nick_text)
            postLikeText = itemView.findViewById(R.id.post_likes_text)
            postDescriptionText = itemView.findViewById(R.id.post_description_text)
            imageHolder = itemView.findViewById(R.id.image_holder)
            loadingBar = itemView.findViewById(R.id.loading_bar)
            likesCounterText = itemView.findViewById(R.id.likes_counter_text)
            commentsButton = itemView.findViewById(R.id.post_comments_button)
        }
    }

    // convenience method for getting data at click position
    fun getPost(id: Int): Post {
        return mData[id]
    }

    // allows clicks events to be caught
    fun setClickListener(itemClickListener: ItemClickListener?) {
        mClickListener = itemClickListener
    }

    fun removeFromList(position: Int) {
        mData.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, mData.size)
    }

    fun setHeartClickListener(heartClickListener: HeartClickListener) {
        mHeartListener = heartClickListener
    }

    interface HeartClickListener {
        fun onHeartClick(position: Int)
    }
}