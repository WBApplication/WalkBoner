/*
 * Copyright (c) 2022 - WalkBoner.
 * LoadingView.kt
 */

package com.fusoft.walkboner.views

import android.animation.Animator
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import com.fusoft.walkboner.R
import com.google.android.material.textview.MaterialTextView

class LoadingView : FrameLayout {

    private var isShowing: Boolean = false
    private var animationDuration: Long = 250

    lateinit var mMessageTextView: MaterialTextView
    lateinit var mTitleTextView: MaterialTextView

    constructor(context: Context) : this(context, null) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        this.visibility = View.GONE
        this.alpha = 0f
        inflate(context, R.layout.view_loading, this)

        mTitleTextView = this.findViewById(R.id.title_text);
        mMessageTextView = this.findViewById(R.id.description_text)
    }

    fun show() {
        if (!isShowing) {
            this.visibility = View.VISIBLE
            isShowing = true
            this.alpha = 0f
            this.animate().setDuration(animationDuration).alpha(1f)
                .setInterpolator(DecelerateInterpolator()).start()
        }
    }

    fun hide() {
        if (isShowing) {
            isShowing = false
            this.animate().setDuration(animationDuration).alpha(0f)
                .setInterpolator(DecelerateInterpolator())
                .setListener(object : Animator.AnimatorListener {
                    override fun onAnimationStart(animation: Animator) {

                    }

                    override fun onAnimationEnd(animation: Animator) {
                        this@LoadingView.visibility = View.GONE
                    }

                    override fun onAnimationCancel(animation: Animator) {

                    }

                    override fun onAnimationRepeat(animation: Animator) {

                    }
                }).start()
        }
    }

    fun setTitle(text: String) {
        mTitleTextView.text = text
    }

    fun setMessage(text: String) {
        mMessageTextView.text = text
    }

    fun setVisibilityAnimationDuration(duration: Long) {
        animationDuration = duration
    }
}