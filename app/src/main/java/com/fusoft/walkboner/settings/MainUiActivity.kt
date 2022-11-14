/*
 * Copyright (c) 2022 - WalkBoner.
 * MainUiActivity.kt
 */

package com.fusoft.walkboner.settings

import android.app.Activity
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import com.fusoft.walkboner.databinding.ActivityMainUiBinding
import com.fusoft.walkboner.extends.resetApp
import com.fusoft.walkboner.views.dialogs.ResetAppListener
import com.fusoft.walkboner.views.dialogs.ShowResetAppDialog

class MainUiActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainUiBinding
    lateinit var settings: Settings

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainUiBinding.inflate(layoutInflater)
        settings = Settings(this)

        setContentView(binding.root)

        setup()
    }

    private fun setup() {
        if (settings.isNavigationRailEnabled) {
            binding.leftNavigationViewCheckbox.isChecked = true
        }

        binding.bottomNavigationViewCheckbox.setOnClickListener {
            settings.isNavigationRailEnabled = false
            resetAppDialog()
        }

        binding.leftNavigationViewCheckbox.setOnClickListener {
            settings.isNavigationRailEnabled = true
            resetAppDialog()
        }
    }

    private fun resetAppDialog() {
        ShowResetAppDialog(this@MainUiActivity, layoutInflater, object: ResetAppListener {
            override fun OnResetApp() {
                resetApp(this@MainUiActivity)
            }

            override fun OnDismiss() {
                // Do nothing
            }

        })
    }
}