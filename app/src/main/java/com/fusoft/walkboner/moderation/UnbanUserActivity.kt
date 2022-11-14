/*
 * Copyright (c) 2022 - WalkBoner.
 * UnbanUserActivity.kt
 */

package com.fusoft.walkboner.moderation

import android.animation.LayoutTransition
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.fusoft.walkboner.R
import com.fusoft.walkboner.adapters.recyclerview.BannedUsersAdapter
import com.fusoft.walkboner.database.funcions.GetBannedUsers
import com.fusoft.walkboner.database.funcions.UnbanUser
import com.fusoft.walkboner.database.funcions.UnbanUserListener
import com.fusoft.walkboner.databinding.ActivityUnbanUserBinding
import com.fusoft.walkboner.models.User
import com.fusoft.walkboner.utils.AnimateChanges
import de.dlyt.yanndroid.oneui.dialog.ProgressDialog
import de.dlyt.yanndroid.oneui.layout.ToolbarLayout
import de.dlyt.yanndroid.oneui.menu.MenuItem
import de.dlyt.yanndroid.oneui.sesl.recyclerview.LinearLayoutManager
import de.dlyt.yanndroid.oneui.view.Toast

class UnbanUserActivity : AppCompatActivity() {
    lateinit var binding: ActivityUnbanUserBinding

    var layoutManager: LinearLayoutManager? = null
    var adapter: BannedUsersAdapter? = null
    var loadingDialog: ProgressDialog? = null
    var usersList: List<User>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityUnbanUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
        setup()
    }

    override fun onDestroy() {
        layoutManager = null
        adapter = null
        usersList = null
        loadingDialog = null

        super.onDestroy()
    }

    override fun onBackPressed() {
        if (binding.unbanUserToolbar.isSearchMode && !usersList.isNullOrEmpty()) {
            binding.unbanUserToolbar.dismissSearchMode()
        } else {
            super.onBackPressed()
        }
    }

    private fun init() {
        createLoadingDialog()

        layoutManager = LinearLayoutManager(this@UnbanUserActivity)
        binding.bannedUserRecyclerView.layoutManager = layoutManager
        binding.unbanUserToolbar.inflateToolbarMenu(R.menu.unban_user_menu)
        binding.unbanUserToolbar.setOnToolbarMenuItemClickListener { item ->
            if (item?.itemId?.equals(R.id.search_button) == true) {
                binding.unbanUserToolbar.showSearchMode()
            }
            false
        }

        binding.unbanUserToolbar.setSearchModeListener(object : ToolbarLayout.SearchModeListener() {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filter(s.toString())
                super.onTextChanged(s, start, before, count)
            }

            override fun onKeyboardSearchClick(s: CharSequence?) {
                filter(s.toString())
                super.onKeyboardSearchClick(s)
            }

            override fun onSearchDismissed(search_edittext: EditText?) {
                adapter?.filterList(usersList)
                binding.searchQueryNotFinded.visibility = View.GONE
                binding.bannedUserRecyclerView.visibility = View.VISIBLE
                super.onSearchDismissed(search_edittext)
            }
        })
    }

    private fun setup() {
        binding.unbanUserToolbar.setNavigationButtonOnClickListener {
            if (binding.unbanUserToolbar.isSearchMode) {
                binding.unbanUserToolbar.dismissSearchMode()
            } else {
                finish()
            }
        }

        GetBannedUsers.Get(object : GetBannedUsers.BannedUsersGetListener {
            override fun OnReceived(users: MutableList<User>?) {
                usersList = users
                adapter = BannedUsersAdapter(this@UnbanUserActivity, users)
                binding.bannedUserRecyclerView.adapter = adapter
                binding.loadingProgressBar.visibility = View.GONE
                binding.bannedUserRecyclerView.visibility = View.VISIBLE

                adapter!!.setListener { userUid, position ->
                    loadingDialog?.show()
                    UnbanUser.Unban(userUid, object: UnbanUserListener{
                        override fun OnUserUnbanned() {
                            adapter?.deleteFromList(position)
                            loadingDialog?.dismiss()
                        }

                        override fun OnError(reason: String?) {
                            loadingDialog?.dismiss()
                            Toast.makeText(this@UnbanUserActivity, reason, Toast.LENGTH_LONG).show();
                            super.OnError(reason)
                        }
                    })
                }
            }

            override fun OnError(error: String?) {
                super.OnError(error)
            }
        })
    }

    private fun createLoadingDialog() {
        loadingDialog = ProgressDialog(this@UnbanUserActivity)
        loadingDialog?.isIndeterminate = true
        loadingDialog?.setCancelable(false)
        loadingDialog?.setCanceledOnTouchOutside(false)
        loadingDialog?.setProgressStyle(ProgressDialog.STYLE_CIRCLE)
    }

    private fun filter(text: String) {
        val filteredList: ArrayList<User> = ArrayList()

        for (user in usersList!!) {
            if (user.userName.lowercase().contains(text.lowercase())) {
                filteredList.add(user)
            }

            if (filteredList.isEmpty()) {
                binding.searchQueryNotFinded.visibility = View.VISIBLE
                binding.bannedUserRecyclerView.visibility = View.GONE
            } else {
                binding.searchQueryNotFinded.visibility = View.GONE
                binding.bannedUserRecyclerView.visibility = View.VISIBLE
                adapter?.filterList(filteredList)
            }
        }
    }
}