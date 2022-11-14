package com.fusoft.walkboner.adapters.recyclerview

import android.content.Context
import android.content.Intent
import com.fusoft.walkboner.models.Influencer
import android.view.LayoutInflater
import android.view.ViewGroup
import com.fusoft.walkboner.R
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.transition.Transition
import com.fusoft.walkboner.MainActivity
import com.fusoft.walkboner.ProfileActivity
import com.google.android.material.textview.MaterialTextView

class InfluencersAdapter(private val context: Context, data: List<Influencer>) :
    RecyclerView.Adapter<InfluencersAdapter.ViewHolder>() {
    private val mData: List<Influencer>
    private val mInflater: LayoutInflater
    private var mClickListener: ItemClickListener? = null

    // data is passed into the constructor
    init {
        mInflater = LayoutInflater.from(context)
        mData = data
    }

    // inflates the row layout from xml when needed
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.item_influencer, parent, false)
        return ViewHolder(view)
    }

    // binds the data to the TextView in each row
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val influencer = mData[position]
        holder.influencerNickText.text = influencer.influencerNickName
        Glide.with(context)
            .load(influencer.influencerAvatar) //.transform(new PositionedCropTransformation(context, 1f, 0.5f))
            .into(object : CustomTarget<Drawable?>() {
                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable?>?
                ) {
                    holder.influencerImage.setImageDrawable(resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) {}
            })
        holder.itemView.setOnClickListener { v: View? ->
            if (mClickListener != null) mClickListener!!.onItemClick(
                influencer,
                position
            )
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

        init {
            influencerImage = itemView.findViewById(R.id.influencer_image)
            influencerNickText = itemView.findViewById(R.id.influencer_nick_text)
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
}