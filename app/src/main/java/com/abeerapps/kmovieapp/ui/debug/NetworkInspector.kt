package com.abeerapps.kmovieapp.ui.debug

import android.content.Context
import android.content.Intent

/**
 * Opens the HTTP inspector. Only bound in debug builds (see the debug source set); release
 * builds have no implementation, so the drawer entry is hidden there.
 */
fun interface NetworkInspector {
    fun launchIntent(context: Context): Intent
}
