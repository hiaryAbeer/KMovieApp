package com.abeerapps.kmovieapp

import android.app.Application
import com.abeerapps.kmovieapp.domain.repository.SettingsRepository
import com.abeerapps.kmovieapp.ui.common.applyAsNightMode
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import timber.log.Timber

@HiltAndroidApp
class KMovieApplication : Application() {

    @Inject
    lateinit var settingsRepository: SettingsRepository

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
        // Before any activity starts, so the first frame already uses the chosen theme.
        settingsRepository.themeMode.value.applyAsNightMode()
    }
}
