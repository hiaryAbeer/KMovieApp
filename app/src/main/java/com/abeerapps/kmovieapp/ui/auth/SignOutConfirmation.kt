package com.abeerapps.kmovieapp.ui.auth

import android.content.Context
import com.abeerapps.kmovieapp.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

fun Context.showSignOutConfirmation(onSignOut: () -> Unit) {
    MaterialAlertDialogBuilder(this)
        .setTitle(R.string.sign_out_title)
        .setMessage(R.string.sign_out_message)
        .setPositiveButton(R.string.sign_out) { _, _ -> onSignOut() }
        .setNegativeButton(android.R.string.cancel, null)
        .show()
}
