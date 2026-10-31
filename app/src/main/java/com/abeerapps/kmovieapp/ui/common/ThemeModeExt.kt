package com.abeerapps.kmovieapp.ui.common

import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatDelegate
import com.abeerapps.kmovieapp.R
import com.abeerapps.kmovieapp.domain.model.ThemeMode

fun ThemeMode.applyAsNightMode() {
    AppCompatDelegate.setDefaultNightMode(
        when (this) {
            ThemeMode.SYSTEM -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            ThemeMode.LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
            ThemeMode.DARK -> AppCompatDelegate.MODE_NIGHT_YES
        },
    )
}

@get:StringRes
val ThemeMode.labelRes: Int
    get() = when (this) {
        ThemeMode.SYSTEM -> R.string.theme_system
        ThemeMode.LIGHT -> R.string.theme_light
        ThemeMode.DARK -> R.string.theme_dark
    }
