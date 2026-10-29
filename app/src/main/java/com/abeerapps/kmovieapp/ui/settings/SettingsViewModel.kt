package com.abeerapps.kmovieapp.ui.settings

import androidx.lifecycle.ViewModel
import com.abeerapps.kmovieapp.domain.model.ThemeMode
import com.abeerapps.kmovieapp.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    val themeMode: StateFlow<ThemeMode> = settingsRepository.themeMode

    fun setThemeMode(mode: ThemeMode) = settingsRepository.setThemeMode(mode)
}
