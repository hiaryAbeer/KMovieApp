package com.abeerapps.kmovieapp.ui.settings

import androidx.lifecycle.ViewModel
import com.abeerapps.kmovieapp.domain.model.ThemeMode
import com.abeerapps.kmovieapp.domain.usecase.ObserveThemeModeUseCase
import com.abeerapps.kmovieapp.domain.usecase.SetThemeModeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    observeThemeMode: ObserveThemeModeUseCase,
    private val setThemeModeUseCase: SetThemeModeUseCase,
) : ViewModel() {

    val themeMode: StateFlow<ThemeMode> = observeThemeMode()

    fun setThemeMode(mode: ThemeMode) = setThemeModeUseCase(mode)
}
