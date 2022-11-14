package com.fusoft.walkboner.adapters.recyclerview

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.fusoft.walkboner.R
import com.fusoft.walkboner.models.Notification
import com.google.android.material.textview.MaterialTextView

class NotificationsAdapter(private val context: Context, data: ArrayList<Notification>) :
    RecyclerView.Adapter<NotificationsAdapter.ViewHolder>() {
    private val mData: ArrayList<Notification>
    private val mInflater: LayoutInflater
    private var mClickListener: ItemClickListener? = null

    // data is passed into the constructor
    init {
        mInflater = LayoutInflater.from(context)
        mData = data
    }

    // inflates the row layout from xml when needed
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.item_notification, parent, false)
        return ViewHolder(view)
    }

    // binds the data to the TextView in each row
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val notification = mData[position]
        holder.titleTextView.text = notification.notificationTitle
        holder.messageTextView.text = notification.notificationDescription
        holder.deleteButton.setOnClickListener { v: View? ->
            if (mClickListener != null) mClickListener!!.onDeleteClick(
                notification,
                position
            )
        }
    }

    // total number of rows
    override fun getItemCount(): Int {
        return mData.size
    }

    // stores and recycles views as they are scrolled off screen
    inner class ViewHolder internal constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var titleTextView: MaterialTextView
        var messageTextView: MaterialTextView
        var deleteButton: ImageView

        init {
            titleTextView = itemView.findViewById(R.id.title_text)
            messageTextView = itemView.findViewById(R.id.description_text)
            deleteButton = itemView.findViewById(R.id.delete_notification_button)
        }
    }

    // convenience method for getting data at click position
    fun getNotification(id: Int): Notification {
        return mData[id]
    }

    fun deleteAt(position: Int) {
        mData.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, mData.size)
    }

    // allows clicks events to be caught
    fun setClickListener(itemClickListener: com.fusoft.walkboner.adapters.recyclerview.NotificationsAdapter.ItemClickListener) {
        mClickListener = itemClickListener
    }

    // parent activity will implement this method to respond to click events
    interface ItemClickListener {
        fun onDeleteClick(notification: Notification?, position: Int)
    }
}