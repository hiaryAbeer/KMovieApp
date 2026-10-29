package com.abeerapps.kmovieapp.ui.auth

import androidx.fragment.app.Fragment
import com.abeerapps.kmovieapp.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/** Explains why signing in is needed before sending the user to TMDB. */
fun Fragment.showSignInPrompt(onSignIn: () -> Unit) {
    MaterialAlertDialogBuilder(requireContext())
        .setTitle(R.string.sign_in_title)
        .setMessage(R.string.sign_in_message)
        .setPositiveButton(R.string.sign_in) { _, _ -> onSignIn() }
        .setNegativeButton(R.string.not_now, null)
        .show()
}
