package com.fusoft.walkboner.settings

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.fusoft.walkboner.databinding.ActivityPostUiBinding
import com.fusoft.walkboner.settings.Settings.POST_VIEW_TYPE
import com.fusoft.walkboner.R.string as AppText

class PostsUiActivity : AppCompatActivity() {
    lateinit var binding: ActivityPostUiBinding

    lateinit var settings: Settings

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostUiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        settings = Settings(applicationContext)

        setup()
    }

    fun setup() {
        bigPostValidate()

        if (settings.postViewType.equals(POST_VIEW_TYPE.BIG_POST)) {
            binding.bigPostViewCheckbox.isChecked = true
        }

        binding.discreteSwitch.isChecked = settings.isPrivateMode
        binding.snapPostsSwitch.isChecked = settings.shouldSnapPosts()

        binding.smallPostViewCheckbox.setOnClickListener {
            settings.postViewType = POST_VIEW_TYPE.SMALL_POST
        }

        binding.bigPostViewCheckbox.setOnClickListener {
            settings.postViewType = POST_VIEW_TYPE.BIG_POST
        }

        binding.discreteSwitch.setOnClickListener {
            settings.isPrivateMode = binding.discreteSwitch.isChecked
        }

        binding.snapPostsSwitch.setOnClickListener {
            settings.toggleSnapPosts(binding.snapPostsSwitch.isChecked)
            binding.bigPostViewCheckbox.isEnabled = binding.snapPostsSwitch.isChecked
            bigPostValidate()
        }
    }

    private fun bigPostValidate() {
        if (!settings.shouldSnapPosts() || !binding.snapPostsSwitch.isChecked) {
            binding.bigPostViewCheckbox.isEnabled = false
            binding.postDisplayDescription.text = getString(AppText.post_display_description) +
                    "\n\nNa pełnym ekranie działa tylko jeśli masz włączoną funkcje przyciągania postów."
            binding.smallPostViewCheckbox.isChecked = true
            settings.postViewType = POST_VIEW_TYPE.SMALL_POST
        } else {
            binding.bigPostViewCheckbox.isEnabled = true
            binding.postDisplayDescription.text = getString(AppText.post_display_description)
        }
    }
}