package com.fusoft.walkboner.fragments

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.fusoft.walkboner.AddInfluencerActivity
import com.fusoft.walkboner.MainActivity
import com.fusoft.walkboner.PersonAlbumsActivity
import com.fusoft.walkboner.R
import com.fusoft.walkboner.adapters.recyclerview.InfluencersAdapter
import com.fusoft.walkboner.adapters.recyclerview.ItemClickListener
import com.fusoft.walkboner.database.funcions.GetInfluencers
import com.fusoft.walkboner.database.funcions.InfluencersListener
import com.fusoft.walkboner.databinding.FragmentCelebritiesBinding
import com.fusoft.walkboner.models.Influencer

class CelebritiesFragment : Fragment(R.layout.fragment_celebrities) {
    var binding: FragmentCelebritiesBinding? = null

    var adapter: InfluencersAdapter? = null

    override fun onDestroyView() {
        binding = null
        adapter = null

        super.onDestroyView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentCelebritiesBinding.bind(view)

        initView()
    }

    private fun initView() {
        //AnimateChanges.forLinear(mainLinear)
        val layoutManager = GridLayoutManager(getActivity(), 3)
        binding?.celebritiesRecyclerView?.layoutManager = layoutManager
        loadData()
        binding?.influencersSwipeRefresh?.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener { loadData() })
        binding?.addInfluencerButton?.setOnClickListener { v: View? -> openActivity() }
    }

    private fun loadData() {
        binding?.loadingProgressBar?.visibility = View.VISIBLE
        binding?.celebritiesRecyclerView?.visibility = View.GONE
        binding?.addInfluencerButton?.visibility = View.GONE
        binding?.celebritiesRecyclerView?.adapter = null
        val getter = GetInfluencers(GetInfluencers.ORDER_MOST_VIEWED, object : InfluencersListener {
            override fun OnDataReceived(influencers: List<Influencer>) {
                adapter = activity?.let { InfluencersAdapter(it, influencers) }
                binding?.celebritiesRecyclerView?.adapter = adapter
                binding?.loadingProgressBar?.visibility = View.GONE
                binding?.celebritiesRecyclerView?.visibility = View.VISIBLE
                binding?.addInfluencerButton?.visibility = View.VISIBLE
                if (binding?.influencersSwipeRefresh?.isRefreshing == true) {
                    binding?.influencersSwipeRefresh?.isRefreshing = false
                }
                adapter!!.setClickListener(object : ItemClickListener {
                    override fun onItemClick(influencer: Influencer, position: Int) {
                        var intent: Intent? =
                            Intent(getActivity(), PersonAlbumsActivity::class.java)
                        intent!!.putExtra("influencerNick", influencer.influencerNickName)
                        intent.putExtra(
                            "influencerFullName",
                            influencer.influencerFirstName + " " + influencer.influencerLastName
                        )
                        intent.putExtra("influencerAvatar", influencer.influencerAvatar)
                        intent.putExtra("influencerUid", influencer.influencerUid)
                        intent.putExtra("influencerYouTube", influencer.influencerYouTubeLink)
                        intent.putExtra("influencerInstagram", influencer.influencerInstagramLink)
                        startActivity(intent)
                        intent = null
                    }
                })
            }

            override fun OnError(reason: String) {}
        })
    }

    private fun openActivity() {
        (activity as MainActivity?)!!.openActivity(AddInfluencerActivity::class.java, false)
    }
}