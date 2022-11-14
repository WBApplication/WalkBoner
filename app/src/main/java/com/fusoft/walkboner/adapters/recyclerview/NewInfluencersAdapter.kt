package com.fusoft.walkboner.adapters.recyclerview

import android.content.Context
import android.content.Intent
import com.fusoft.walkboner.models.Influencer
import android.view.LayoutInflater
import android.view.ViewGroup
import com.fusoft.walkboner.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.DocumentSnapshot
import com.fusoft.walkboner.utils.CopyTextToClipboard
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.transition.Transition
import com.fusoft.walkboner.MainActivity
import com.fusoft.walkboner.ProfileActivity
import com.fusoft.walkboner.models.User
import com.google.android.material.textview.MaterialTextView
import com.google.android.material.button.MaterialButton
import java.lang.Boolean

class NewInfluencersAdapter(private val context: Context, data: MutableList<Influencer>) :
    RecyclerView.Adapter<NewInfluencersAdapter.ViewHolder>() {
    private val mData: MutableList<Influencer>
    private val mInflater: LayoutInflater
    private var mClickListener: ItemClickListener? = null

    // data is passed into the constructor
    init {
        mInflater = LayoutInflater.from(context)
        mData = data
    }

    // inflates the row layout from xml when needed
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.item_influencer_post, parent, false)
        return ViewHolder(view)
    }

    // binds the data to the TextView in each row
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val influencer = mData[position]
        val firestore = FirebaseFirestore.getInstance()
        firestore.collection("users").whereEqualTo("userUid", influencer.influencerAddedBy).get()
            .addOnSuccessListener { queryDocumentSnapshots: QuerySnapshot ->
                for (doc in queryDocumentSnapshots.documents) {
                    val userData = User()
                    userData.userName = doc["username"].toString()
                    userData.userAvatar = doc["avatar"].toString()
                    userData.isUserVerified = Boolean.parseBoolean(doc["isVerified"].toString())
                    userData.isUserModerator = Boolean.parseBoolean(doc["isMod"].toString())
                    userData.isUserAdmin = Boolean.parseBoolean(doc["isAdmin"].toString())
                    userData.isUserBanned = Boolean.parseBoolean(doc["isBanned"].toString())
                    holder.influencerNickText.text = influencer.influencerNickName
                    holder.celebrityNameText.text = influencer.influencerFirstName
                    holder.celebritySurnameText.text = influencer.influencerLastName
                    holder.requestUserNameText.text = userData.userName
                    holder.requestUserUidButton.setOnClickListener { v: View? ->
                        var copyTextToClipboard: CopyTextToClipboard? = CopyTextToClipboard(
                            context, influencer.influencerAddedBy
                        )
                        copyTextToClipboard = null
                    }
                    holder.acceptButton.setOnClickListener { v: View? ->
                        mClickListener!!.onAcceptClick(
                            holder.itemView,
                            position
                        )
                    }
                    holder.declineButton.setOnClickListener { v: View? ->
                        mClickListener!!.onDeclineClick(
                            holder.itemView,
                            position
                        )
                    }
                    Glide.with(context).load(influencer.influencerAvatar)
                        .into(object : CustomTarget<Drawable?>() {
                            override fun onLoadCleared(placeholder: Drawable?) {}
                            override fun onResourceReady(
                                resource: Drawable,
                                transition: Transition<in Drawable?>?
                            ) {
                                holder.influencerImage.setImageDrawable(resource)
                            }
                        })
                }
            }
    }

    // total number of rows
    override fun getItemCount(): Int {
        return mData.size
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
        var influencerImage: ImageView
        var influencerNickText: MaterialTextView
        var celebrityNameText: MaterialTextView
        var celebritySurnameText: MaterialTextView
        var requestUserNameText: MaterialTextView
        var requestUserUidButton: MaterialTextView
        var acceptButton: MaterialButton
        var declineButton: MaterialButton

        init {
            influencerImage = itemView.findViewById(R.id.influencer_image)
            influencerNickText = itemView.findViewById(R.id.influencer_nick_text)
            celebrityNameText = itemView.findViewById(R.id.celebrity_name_text)
            celebritySurnameText = itemView.findViewById(R.id.celebrity_surname_text)
            requestUserNameText = itemView.findViewById(R.id.request_user_name_text)
            requestUserUidButton = itemView.findViewById(R.id.request_user_uid_button)
            acceptButton = itemView.findViewById(R.id.accept_profile_button)
            declineButton = itemView.findViewById(R.id.delete_profile_button)
        }
    }

    // convenience method for getting data at click position
    fun getInfluencer(id: Int): Influencer {
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

    // parent activity will implement this method to respond to click events
    interface ItemClickListener {
        fun onAcceptClick(view: View?, position: Int)
        fun onDeclineClick(view: View?, position: Int)
    }
}