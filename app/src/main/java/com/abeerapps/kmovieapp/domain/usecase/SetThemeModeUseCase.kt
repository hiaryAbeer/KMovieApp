package com.abeerapps.kmovieapp.domain.usecase

import com.abeerapps.kmovieapp.domain.model.ThemeMode
import com.abeerapps.kmovieapp.domain.repository.SettingsRepository
import javax.inject.Inject

class SetThemeModeUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository,
) {
    operator fun invoke(mode: ThemeMode) = settingsRepository.setThemeMode(mode)
}
