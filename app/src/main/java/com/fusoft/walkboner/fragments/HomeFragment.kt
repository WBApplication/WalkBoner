/*
 * Copyright (c) 2022 - WalkBoner.
 * HomeFragment.kt
 */

package com.fusoft.walkboner.fragments

import android.animation.Animator
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fusoft.walkboner.MainActivity
import com.fusoft.walkboner.R
import com.fusoft.walkboner.SettingsActivity
import com.fusoft.walkboner.UpdateActivity
import com.fusoft.walkboner.adapters.recyclerview.PostsAdapter
import com.fusoft.walkboner.database.funcions.GetPosts
import com.fusoft.walkboner.databinding.FragmentHomeBinding
import com.fusoft.walkboner.models.Post
import com.fusoft.walkboner.settings.Settings
import com.fusoft.walkboner.utils.AppUpdate
import com.fusoft.walkboner.utils.StartSnap

class HomeFragment : Fragment(R.layout.fragment_home) {
    var binding: FragmentHomeBinding? = null
    var settings: Settings? = null
    var layoutManager: LinearLayoutManager? = null
    var adapter: PostsAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentHomeBinding.bind(view)

        init()
        setup()
    }

    override fun onDestroyView() {
        binding = null
        settings = null
        layoutManager = null
        adapter = null

        super.onDestroyView()
    }

    private fun init() {
        settings = Settings(activity)
        layoutManager = LinearLayoutManager(activity)
    }

    private fun setup() {
        AppUpdate.checkForUpdate(object: AppUpdate.UpdateListener {
            override fun OnUpdateAvailable(
                version: String?,
                changeLog: String?,
                downloadUrl: String?,
                isRequired: Boolean
            ) {
                binding?.updateAppButton?.visibility = View.VISIBLE
                animateUpdateButton()

                binding?.updateAppButton?.setOnClickListener {
                    var intent: Intent? = Intent(activity, UpdateActivity::class.java)
                    startActivity(intent)
                    intent = null
                }
            }
        })

        binding?.postsRecyclerView?.layoutManager = layoutManager

        binding?.settingsButton?.setOnClickListener {
            (activity as MainActivity).openActivity(SettingsActivity::class.java, false)
        }

        if (settings!!.shouldSnapPosts()) {
            val linearSnapHelper = StartSnap()
            linearSnapHelper.attachToRecyclerView(binding?.postsRecyclerView)
        }

        loadPosts()

        binding?.homeSwipeRefreshLayout?.setOnRefreshListener {
            loadPosts()
        }
    }

    private fun animateUpdateButton() {
        binding?.updateAppButton?.animate()?.scaleX(0.85f)?.scaleY(0.85f)?.setDuration(600)?.setListener(object: Animator.AnimatorListener{
            override fun onAnimationStart(animation: Animator) {

            }

            override fun onAnimationEnd(animation: Animator) {
                binding?.updateAppButton?.animate()?.scaleX(1f)?.scaleY(1f)?.setDuration(600)?.setListener(object: Animator.AnimatorListener{
                    override fun onAnimationStart(animation: Animator) {

                    }

                    override fun onAnimationEnd(animation: Animator) {
                        animateUpdateButton()
                    }

                    override fun onAnimationCancel(animation: Animator) {

                    }

                    override fun onAnimationRepeat(animation: Animator) {

                    }
                })?.start()
            }

            override fun onAnimationCancel(animation: Animator) {

            }

            override fun onAnimationRepeat(animation: Animator) {

            }
        })?.start()
    }

    private fun loadPosts() {
        GetPosts.get(object : GetPosts.PostsListener {
            override fun OnLoaded(posts: List<Post>) {
                if (binding?.homeSwipeRefreshLayout?.isRefreshing == true) {
                    binding?.homeSwipeRefreshLayout?.isRefreshing = false
                }

                adapter = settings?.let { activity?.let { it1 -> PostsAdapter(it1, posts as ArrayList<Post>, it) } }!!
                binding?.postsRecyclerView?.adapter = adapter
            }

            override fun OnError(error: String) {}
        })
    }

    public fun getRecyclerView(): RecyclerView? {
        return binding?.postsRecyclerView
    }
}