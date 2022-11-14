package com.fusoft.walkboner.adapters.recyclerview

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.fusoft.walkboner.FullPhotoViewerActivity
import com.fusoft.walkboner.PersonAlbumsActivity
import com.fusoft.walkboner.ProfileActivity
import com.fusoft.walkboner.R
import com.fusoft.walkboner.database.funcions.GetPopularPosts
import com.fusoft.walkboner.database.funcions.LikePost
import com.fusoft.walkboner.models.Post
import com.fusoft.walkboner.models.User
import com.fusoft.walkboner.moderation.BanUserActivity
import com.fusoft.walkboner.settings.Settings
import com.fusoft.walkboner.singletons.UserSingletone
import com.fusoft.walkboner.utils.CopyTextToClipboard
import com.fusoft.walkboner.views.Avatar
import com.fusoft.walkboner.views.bottomsheets.PostCommentsBSD
import com.fusoft.walkboner.views.dialogs.ReportDialog
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.squareup.picasso.Picasso
import com.yy.mobile.rollingtextview.CharOrder
import com.yy.mobile.rollingtextview.RollingTextView
import com.yy.mobile.rollingtextview.strategy.Direction
import com.yy.mobile.rollingtextview.strategy.Strategy.CarryBitAnimation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kr.co.prnd.readmore.ReadMoreTextView
import java.lang.Boolean
import kotlin.Any
import kotlin.Int
import kotlin.String
import kotlin.coroutines.CoroutineContext
import kotlin.let
import kotlin.toString

class PostsAdapter(private val context: Context, data: ArrayList<Post>, settings: Settings) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(), CoroutineScope {
    /* ------------------------------------------ */
    private var job: Job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job
    /* ------------------------------------------ */

    private val mData: ArrayList<Post>
    private val mInflater: LayoutInflater
    private val mClickListener: ItemClickListener? = null
    private var mHeartListener: HeartClickListener? = null
    private val settings: Settings
    private var prevLikesCount = 0
    private val BEST_POSTS_ITEM = 0
    private val POST_ITEM = 1

    // data is passed into the constructor
    init {
        mInflater = LayoutInflater.from(context)
        mData = data
        this.settings = settings
    }

    // inflates the row layout from xml when needed
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == BEST_POSTS_ITEM) {
            return BestPostsViewHolder(
                LayoutInflater.from(context).inflate(R.layout.item_header, parent, false)
            )
        }

        return if (settings.postViewType == Settings.POST_VIEW_TYPE.SMALL_POST) {
            PostsViewHolder(LayoutInflater.from(context).inflate(R.layout.item_post, parent, false))
        } else {
            PostsViewHolder(
                LayoutInflater.from(context).inflate(R.layout.item_post_renew, parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (mData[position].isHeader) {
            (holder as BestPostsViewHolder).bind(position)
        } else {
            (holder as PostsViewHolder).bind(position)
        }
    }

    private inner class BestPostsViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var popularPostsRecyclerView: RecyclerView

        init {
            popularPostsRecyclerView = itemView.findViewById(R.id.popular_posts_recycler_view)
        }

        fun bind(position: Int) {
            val gridLayoutManager = GridLayoutManager(
                context, 1, GridLayoutManager.HORIZONTAL, false
            )

            popularPostsRecyclerView.layoutManager = gridLayoutManager

            launch {
                GetPopularPosts.get(object : GetPopularPosts.PostsListener {
                    override fun OnLoaded(posts: List<Post>?) {
                        val popularPostsAdapter = posts?.let {
                            PopularPostsAdapter(
                                context,
                                it, settings
                            )
                        }

                        popularPostsRecyclerView.adapter = popularPostsAdapter
                    }

                    override fun OnError(error: String?) {}
                }, 3)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (mData[position].isHeader) {
            BEST_POSTS_ITEM
        } else {
            POST_ITEM
        }
    }

    private inner class PostsViewHolder internal constructor(itemView: View) :
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
        var showsCelebrityLinear: LinearLayout
        var showingPersonAvatarImage: ImageView
        var showingPersonNickText: MaterialTextView

        // var imageHolder: ConstraintLayout
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
            // imageHolder = itemView.findViewById(R.id.image_holder)
            loadingBar = itemView.findViewById(R.id.loading_bar)
            likesCounterText = itemView.findViewById(R.id.likes_counter_text)
            commentsButton = itemView.findViewById(R.id.post_comments_button)
            showsCelebrityLinear = itemView.findViewById(R.id.shows_celebrity_linear)
            showingPersonAvatarImage = itemView.findViewById(R.id.showing_person_avatar_image)
            showingPersonNickText = itemView.findViewById(R.id.showing_person_nick_text)
        }

        fun bind(position: Int) {
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
                        userData.isUserAdmin =
                            java.lang.Boolean.parseBoolean(doc["isAdmin"].toString())
                        userData.isUserBanned =
                            java.lang.Boolean.parseBoolean(doc["isBanned"].toString())
                        personNickText.text = userData.userName
                        personMentionNickText.text = "@" + userData.userName
                        postDescriptionText.text = post.postDescription
                        personAvatarImage.setOnClickListener { view: View? -> openProfile(post.userUid) }
                        personNickText.setOnClickListener { view: View? -> openProfile(post.userUid) }
                        personMentionNickText.setOnClickListener { view: View? -> openProfile(post.userUid) }
                        postImage.setOnClickListener { view: View? ->
                            val intent = Intent(
                                context, FullPhotoViewerActivity::class.java
                            )
                            intent.putExtra("imageUrl", post.postImage)
                            intent.putExtra("type", "image")
                            context.startActivity(intent)
                        }

                        val showRelatedPerson =
                            !post.showsCelebrity.equals("null") && !post.showsCelebrity.equals("celebrityUid")

                        if (showRelatedPerson) {
                            firestore.collection("influencers").document(post.showsCelebrity).get()
                                .addOnSuccessListener { document ->
                                    if (document != null || !Boolean.parseBoolean(
                                            document?.get("isHidden").toString()
                                        )
                                    ) {
                                        showsCelebrityLinear.visibility = View.VISIBLE
                                        showingPersonNickText.text =
                                            document.getString("influencerNickName")
                                        Picasso.get().load(document.getString("influencerAvatar"))
                                            .into(showingPersonAvatarImage)

                                        showingPersonNickText.setOnClickListener {
                                            openAlbum(
                                                document.getString("influencerNickName").toString(),
                                                document.getString("influencerFirstName")
                                                    .toString(),
                                                document.getString("influencerLastName").toString(),
                                                document.getString("influencerAvatar").toString(),
                                                document.getString("influencerUid").toString(),
                                                document.getString("influencerYouTubeLink")
                                                    .toString(),
                                                document.getString("influencerInstagramLink")
                                                    .toString()
                                            )
                                        }
                                    }
                                }.addOnFailureListener {
                                showsCelebrityLinear.visibility = View.GONE
                            }
                        }

                        likesCounterText.charStrategy = CarryBitAnimation(Direction.SCROLL_DOWN)
                        likesCounterText.addCharOrder(CharOrder.Number)
                        likesCounterText.animationInterpolator = OvershootInterpolator()
                        val requestOptions =
                            RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        val glideListener: RequestListener<Drawable?> =
                            object : RequestListener<Drawable?> {
                                override fun onLoadFailed(
                                    e: GlideException?,
                                    model: Any?,
                                    target: Target<Drawable?>?,
                                    isFirstResource: kotlin.Boolean
                                ): kotlin.Boolean {
                                    return false
                                }

                                override fun onResourceReady(
                                    resource: Drawable?,
                                    model: Any?,
                                    target: Target<Drawable?>?,
                                    dataSource: DataSource?,
                                    isFirstResource: kotlin.Boolean
                                ): kotlin.Boolean {
                                    postImage.visibility = View.VISIBLE
                                    loadingBar.visibility = View.GONE
                                    return false
                                }
                            }
                        if (settings.isPrivateMode) { // If PrivateMode -> load private image
                            postImage.visibility = View.VISIBLE
                            loadingBar.visibility = View.GONE
                            Glide.with(context).load(
                                AppCompatResources.getDrawable(
                                    context, R.drawable.walkboner_private
                                )
                            ).into(postImage)
                        } else {
                            Glide.with(context).load(post.postImage).apply(requestOptions)
                                .addListener(glideListener).into(postImage)
                        }
                        if (userData.isUserBanned) {
                            personAvatarImage.setAvatarOwnerPrivileges(Avatar.BANNED)
                        } else if (userData.isUserAdmin) {
                            personAvatarImage.setAvatarOwnerPrivileges(Avatar.ADMIN)
                        } else if (userData.isUserModerator) {
                            personAvatarImage.setAvatarOwnerPrivileges(Avatar.MODERATOR)
                        } else {
                            personAvatarImage.setAvatarOwnerPrivileges(Avatar.USER)
                        }
                        if (!userData.userAvatar.contentEquals("default")) {
                            personAvatarImage.setImageFromUrl(userData.userAvatar)
                        }
                        val popupMenu = PopupMenu(context, moreButton)
                        popupMenu.inflate(R.menu.post_popup_menu)
                        popupMenu.setOnMenuItemClickListener(object :
                            PopupMenu.OnMenuItemClickListener {
                            override fun onMenuItemClick(item: MenuItem?): kotlin.Boolean {
                                if (item?.itemId == R.id.report_popup_button) {
                                    ReportDialog.ReportPostDialog(context, post.postUid)
                                }
                                if (item?.itemId == R.id.copy_post_uid_button) {
                                    var copy: CopyTextToClipboard? =
                                        CopyTextToClipboard(context, post.postUid)
                                    copy = null
                                }
                                if (item?.itemId == R.id.copy_post_user_uid_button) {
                                    var copy: CopyTextToClipboard? =
                                        CopyTextToClipboard(context, post.userUid)
                                    copy = null
                                }
                                if (item?.itemId == R.id.ban_user_button) {
                                    var intentBan: Intent? =
                                        Intent(context, BanUserActivity::class.java)
                                    intentBan?.putExtra("userUid", post.userUid)

                                    context.startActivity(intentBan)
                                }
                                return true
                            }
                        })

                        if (post.userUid!!.contentEquals(UserSingletone.getInstance().user.userUid)) {
                            popupMenu.menu.findItem(R.id.delete_popup_button).isVisible = true
                        }
                        if (UserSingletone.getInstance().user.isUserAdmin || UserSingletone.getInstance().user.isUserModerator) {
                            popupMenu.menu.findItem(R.id.moderation_title).isVisible = true
                            popupMenu.menu.findItem(R.id.copy_post_uid_button).isVisible = true
                            popupMenu.menu.findItem(R.id.copy_post_user_uid_button).isVisible = true
                            popupMenu.menu.findItem(R.id.delete_post_button).isVisible = true
                            popupMenu.menu.findItem(R.id.ban_user_button).isVisible = true
                        }

                        moreButton.setOnClickListener { view: View? -> popupMenu.show() }
                        commentsButton.setOnClickListener { v: View? ->
                            PostCommentsBSD.show(
                                post.postDocumentUid,
                                context
                            )
                        }
                        LikePost().LikeWatcher(post.postDocumentUid) { userLiked: kotlin.Boolean, likesAmount: Int ->
                            post.isUserLikedPost = userLiked
                            if (userLiked) {
                                postLikeButton.setImageResource(R.drawable.ic_favorite_filled_wb)
                            } else {
                                postLikeButton.setImageResource(R.drawable.ic_favorite_wb)
                            }
                            if (likesAmount > prevLikesCount) {
                                likesCounterText.charStrategy =
                                    CarryBitAnimation(Direction.SCROLL_DOWN)
                            } else {
                                likesCounterText.charStrategy =
                                    CarryBitAnimation(Direction.SCROLL_UP)
                            }
                            prevLikesCount = likesAmount
                            postLikeButton.isEnabled = true
                            likesCounterText.animationDuration = 200
                            likesCounterText.setText(likesAmount.toString(), true)
                            postLikeText.text = parseLikes(likesAmount)
                        }

                        // TODO: Dodawaj polubione posty do danyh użytkownika w bazie danych
                        postLikeButton.setOnClickListener(object : OnClickListener {
                            override fun onClick(view: View) {
                                postLikeButton.isEnabled = false
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

    private fun openAlbum(
        nickName: String,
        firstName: String,
        lastName: String,
        avatar: String,
        uid: String,
        youtube: String,
        instagram: String
    ) {
        var intent: Intent? =
            Intent(context, PersonAlbumsActivity::class.java)
        intent!!.putExtra("influencerNick", nickName)
        intent.putExtra(
            "influencerFullName",
            firstName + " " + lastName
        )
        intent.putExtra("influencerAvatar", avatar)
        intent.putExtra("influencerUid", uid)
        intent.putExtra("influencerYouTube", youtube)
        intent.putExtra("influencerInstagram", instagram)
        context.startActivity(intent)
        intent = null
    }

    // convenience method for getting data at click position
    fun getPost(id: Int): Post {
        return mData[id]
    }

    fun removeFromList(position: Int) {
        mData.removeAt(position)
        notifyItemRemoved(position)
    }

    fun setHeartClickListener(heartClickListener: HeartClickListener?) {
        mHeartListener = heartClickListener
    }

    interface HeartClickListener {
        fun onHeartClick(position: Int)
    }
}