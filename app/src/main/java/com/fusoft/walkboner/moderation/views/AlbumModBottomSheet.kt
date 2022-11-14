/*
 * Copyright (c) 2022 - WalkBoner.
 * AlbumModBottomSheet.kt
 */

package com.fusoft.walkboner.moderation.views

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import com.fusoft.walkboner.databinding.BottomSheetModAlbumBinding
import com.fusoft.walkboner.moderation.BanUserActivity
import com.fusoft.walkboner.utils.CopyTextToClipboard
import com.google.android.material.bottomsheet.BottomSheetDialog

fun ShowAlbumModBottomSheet(
    context: Context,
    layoutInflater: LayoutInflater,
    albumUid: String,
    userUid: String
) {
    val binding = BottomSheetModAlbumBinding.inflate(layoutInflater)
    val bottomSheetDialog = BottomSheetDialog(context)
    bottomSheetDialog.setContentView(binding.root)
    bottomSheetDialog.show()

    if (bottomSheetDialog.isShowing) {
        binding.copyAlbumUidButton.setOnClickListener{
            var clipboard: CopyTextToClipboard? = CopyTextToClipboard(context, albumUid)
            clipboard = null

            bottomSheetDialog.dismiss()
        }

        binding.copyUserUidButton.setOnClickListener {
            var clipboard: CopyTextToClipboard? = CopyTextToClipboard(context, userUid)
            clipboard = null

            bottomSheetDialog.dismiss()
        }

        binding.banUserButton.setOnClickListener {
            var intent: Intent? = Intent(context, BanUserActivity::class.java)
            intent?.putExtra("userUid", userUid)

            context.startActivity(intent)
        }
    }
}