package com.fusoft.walkboner

import android.app.ActivityManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import com.fusoft.walkboner.adapters.viewpager.MainViewPager
import com.fusoft.walkboner.auth.Authentication
import com.fusoft.walkboner.databinding.ActivityMainBinding
import com.fusoft.walkboner.fragments.HomeFragment
import com.fusoft.walkboner.models.User
import com.fusoft.walkboner.services.NotificationsService
import com.fusoft.walkboner.settings.Settings
import com.fusoft.walkboner.views.LoadingView
import com.google.android.material.color.DynamicColors

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    var TAG = "MainActivityLog"

    private var auth: Authentication? = null
    private var settings: Settings? = null
    private var user: User? = null
    private var adapter: MainViewPager? = null
    private var currentPageId = 0
    private val isUpdateRequired = false

    private lateinit var navHostFragment: NavHostFragment
    val currentFragment: Fragment?
        get() = navHostFragment.childFragmentManager.fragments.firstOrNull()

    override fun onDestroy() {
        auth = null
        settings = null
        adapter = null
        user = null
        super.onDestroy()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        settings = Settings(this@MainActivity)

        if (settings!!.isNavigationRailEnabled) {
            binding.navigationRailContainer.visibility = View.VISIBLE
            binding.navigationBottom.visibility = View.GONE
        } else {
            binding.navigationRailContainer.visibility = View.GONE
            binding.navigationBottom.visibility = View.VISIBLE
        }

        WindowCompat.setDecorFitsSystemWindows(window, false)

        val defaultTabIndex = "0".toInt()
        currentPageId = R.id.homeFragment2
        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        val graph = navController.navInflater.inflate(R.navigation.main_navigation_graph)
        graph.setStartDestination(
            listOf(
                R.id.homeFragment2,
                R.id.celebritiesFragment,
                R.id.notificationsFragment,
                R.id.userProfileFragment
            )[defaultTabIndex]
        )
        navController.setGraph(graph, null)

        binding.navigationRail.setupWithNavController(navController)
        binding.navigationBottom.setupWithNavController(navController)

        binding.navigationRail.setOnItemSelectedListener { item ->
            if (item.isChecked) {
                // scroll to top
                (currentFragment as? HomeFragment)?.getRecyclerView()?.smoothScrollToPosition(0)
            } else {
                pageChanges(item.itemId)
                onNavDestinationSelected(item, navController)
                item.isChecked = true
            }
            true
        }

        binding.navigationBottom.setOnItemSelectedListener { item ->
            if (item.isChecked) {
                // scroll to top
                (currentFragment as? HomeFragment)?.getRecyclerView()?.smoothScrollToPosition(0)
            } else {
                pageChanges(item.itemId)
                onNavDestinationSelected(item, navController)
                item.isChecked = true
            }
            true
        }

        setContentView(binding.root)
        initView()
        setup()
    }

    override fun onBackPressed() {
        finishAndRemoveTask()
    }

    private fun initView() {

    }

    private fun setup() {
        binding.fab.setOnClickListener {
            var intentFab: Intent? = null
            if (currentPageId == R.id.homeFragment2) {
                intentFab = Intent(this@MainActivity, CreatePostActivity::class.java)
            }
            if (currentPageId == R.id.celebritiesFragment) {
                intentFab = Intent(this@MainActivity, AddInfluencerActivity::class.java)
            }

            startActivity(intentFab)
        }
    }

    fun openActivity(toOpen: Class<*>?, finish: Boolean) {
        startActivity(Intent(this@MainActivity, toOpen))
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        if (finish) {
            finish()
        }
    }

    private fun pageChanges(itemId: Int) {
        val isFabShowing = binding.fab.isShown
        currentPageId = itemId

        if (itemId == R.id.notificationsFragment) {
            if (isFabShowing) binding.fab.hide()
        } else if (itemId == R.id.celebritiesFragment) {
            if (isFabShowing) binding.fab.hide()
        } else if (itemId == R.id.homeFragment2) {
            if (!isFabShowing) binding.fab.show()
        } else {
            if (isFabShowing) binding.fab.hide()
        }
    }

    private fun toggleFab(on: Boolean) {}
    private fun checkIfAppInstalled(name: String): Boolean {
        var available = true
        try {
            // check if available
            packageManager.getPackageInfo(name, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            // if not available set
            // available as false
            available = false
        }
        return available
    }

    private fun showUniloadNonInstalled() {}
    private val isNotificationsServiceRunning: Boolean
        private get() {
            val activityManager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
            for (service in activityManager.getRunningServices(Int.MAX_VALUE)) {
                if (NotificationsService::class.java.name == service.service.className) {
                    return true
                }
            }
            return false
        }

    fun getLoading(): LoadingView {
        return binding.loading
    }
}