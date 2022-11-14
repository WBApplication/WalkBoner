/*
 * Copyright (c) 2022 - WalkBoner.
 * ResetAppDialog.kt
 */

package com.fusoft.walkboner.views.dialogs

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.fusoft.walkboner.databinding.DialogResetAppBinding
import com.fusoft.walkboner.views.dialogs.ChangeProfileDescriptionDialog.ProfileDescriptionDialogListener
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

lateinit var binding: DialogResetAppBinding

fun ShowResetAppDialog(
    context: Context?,
    inflater: LayoutInflater,
    listener: ResetAppListener
) {
    binding = DialogResetAppBinding.inflate(inflater)

    val dialogBuilder = MaterialAlertDialogBuilder(context!!)
    dialogBuilder.setView(binding.root)
    val dialog = dialogBuilder.show()
    if (dialog.isShowing) {
        binding.resetButton.setOnClickListener { v: View? ->
            listener.OnResetApp()
        }

        binding.cancelButton.setOnClickListener { v: View? ->
            listener.OnDismiss()
            dialog.dismiss()
        }
    }
}

interface ResetAppListener {
    fun OnResetApp()

    fun OnDismiss()
}