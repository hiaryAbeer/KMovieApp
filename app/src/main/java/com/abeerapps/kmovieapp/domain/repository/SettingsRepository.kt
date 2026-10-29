package com.abeerapps.kmovieapp.domain.repository

import com.abeerapps.kmovieapp.domain.model.ThemeMode
import kotlinx.coroutines.flow.StateFlow

interface SettingsRepository {
    val themeMode: StateFlow<ThemeMode>

    fun setThemeMode(mode: ThemeMode)
}
