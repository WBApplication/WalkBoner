/*
 * Copyright (c) 2022 - WalkBoner.
 * ResetApp.kt
 */

package com.fusoft.walkboner.extends

import android.app.Activity
import android.content.Intent
import com.fusoft.walkboner.MainActivity
import com.fusoft.walkboner.SplashActivity

fun Activity.resetApp(context: Activity) {
    val intent = Intent(context, SplashActivity::class.java)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(intent)
    if (context is Activity) {
        (context as Activity).finish()
    }
    Runtime.getRuntime().exit(0)
}