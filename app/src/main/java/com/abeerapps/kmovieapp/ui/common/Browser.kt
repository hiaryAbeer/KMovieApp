package com.abeerapps.kmovieapp.ui.common

import android.content.ActivityNotFoundException
import android.content.Context
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.net.toUri

/** Opens [url] in a Custom Tab. Returns false when the device has no browser. */
fun Context.openInCustomTab(url: String): Boolean =
    try {
        CustomTabsIntent.Builder().setShowTitle(true).build().launchUrl(this, url.toUri())
        true
    } catch (_: ActivityNotFoundException) {
        false
    }
