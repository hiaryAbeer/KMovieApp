package com.abeerapps.kmovieapp.ui.common

import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding

/**
 * Adds the system bar insets to this view's padding, on top of the padding set in XML.
 * With [includeIme], the bottom padding also grows to stay above the on-screen keyboard.
 */
fun View.applySystemBarsPadding(
    top: Boolean = false,
    bottom: Boolean = false,
    includeIme: Boolean = false,
) {
    val initialTop = paddingTop
    val initialBottom = paddingBottom
    val types = WindowInsetsCompat.Type.systemBars() or
        if (includeIme) WindowInsetsCompat.Type.ime() else 0
    ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
        val bars = insets.getInsets(types)
        view.updatePadding(
            top = initialTop + if (top) bars.top else 0,
            bottom = initialBottom + if (bottom) bars.bottom else 0,
        )
        insets
    }
}

/** Adds the status bar inset to this view's top margin, on top of the margin set in XML. */
fun View.applyStatusBarMargin() {
    val initialTop = (layoutParams as ViewGroup.MarginLayoutParams).topMargin
    ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
        val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        view.updateLayoutParams<ViewGroup.MarginLayoutParams> { topMargin = initialTop + bars.top }
        insets
    }
}
