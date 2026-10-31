package com.abeerapps.kmovieapp.domain.usecase

import com.abeerapps.kmovieapp.domain.repository.SettingsRepository
import javax.inject.Inject

class ObserveThemeModeUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository,
) {
    operator fun invoke() = settingsRepository.themeMode
}
