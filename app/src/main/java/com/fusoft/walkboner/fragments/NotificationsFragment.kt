/*
 * Copyright (c) 2022 - WalkBoner.
 * NotificationsFragment.java
 */
package com.fusoft.walkboner.fragments

import com.fusoft.walkboner.adapters.recyclerview.NotificationsAdapter
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.fusoft.walkboner.R
import com.fusoft.walkboner.auth.Authentication
import com.fusoft.walkboner.database.funcions.GetNotifications
import com.fusoft.walkboner.database.funcions.GetNotifications.NotificationsListener
import com.fusoft.walkboner.database.funcions.GetNotifications.NotificationsDeleteListener
import com.fusoft.walkboner.databinding.FragmentNotificationsBinding
import com.fusoft.walkboner.models.Notification

class NotificationsFragment : Fragment(R.layout.fragment_notifications) {
    var binding: FragmentNotificationsBinding? = null

    private var authentication: Authentication? = null
    private var adapter: NotificationsAdapter? = null
    private var layoutManager: LinearLayoutManager? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentNotificationsBinding.bind(view)

        initView()
        setup()
    }

    override fun onDestroy() {
        authentication = null
        adapter = null
        layoutManager = null
        binding = null
        super.onDestroy()
    }

    private fun initView() {
        authentication = Authentication(null)
    }

    private fun setup() {
        layoutManager = LinearLayoutManager(activity)
        binding?.notificationsRecyclerView?.layoutManager = layoutManager
        GetNotifications.Get(authentication!!.currentUserUid, true, object : NotificationsListener {
            override fun OnSuccess(notifications: List<Notification>?) {
                //loading!!.dismiss()
                if (notifications != null) {
                    adapter = activity?.let {
                        NotificationsAdapter(
                            it,
                            notifications as ArrayList<Notification>
                        )
                    }
                    adapter!!.setClickListener(object : NotificationsAdapter.ItemClickListener{
                        override fun onDeleteClick(notification: Notification?, position: Int) {
                            //loading!!.show()
                            GetNotifications.Remove(
                                authentication!!.currentUserUid,
                                notification?.notificationUid,
                                object : NotificationsDeleteListener {
                                    override fun OnDeleted() {
                                        //loading!!.dismiss()
                                        adapter!!.deleteAt(position)
                                    }

                                    override fun OnError(reason: String) {
                                        //loading!!.dismiss()
                                        Toast.makeText(
                                            activity,
                                            reason,
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                })
                        }
                    })
                    binding?.notificationsRecyclerView?.adapter = adapter
                } else {
                    Toast.makeText(activity, "puste", Toast.LENGTH_SHORT).show()
                }
            }

            override fun OnError(reason: String) {
                Toast.makeText(activity, reason, Toast.LENGTH_SHORT).show()
            }
        })
    }
}