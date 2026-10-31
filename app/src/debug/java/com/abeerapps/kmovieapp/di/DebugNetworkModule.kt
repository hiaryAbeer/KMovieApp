package com.abeerapps.kmovieapp.di

import android.content.Context
import com.abeerapps.kmovieapp.ui.debug.NetworkInspector
import com.chuckerteam.chucker.api.Chucker
import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.chuckerteam.chucker.api.RetentionManager
import com.localebro.okhttpprofiler.OkHttpProfilerInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import okhttp3.Interceptor

/** Network inspectors for debug builds only. */
@Module
@InstallIn(SingletonComponent::class)
object DebugNetworkModule {

    /** Chucker: in-app HTTP inspector, opened from its notification. */
    @Provides
    @IntoSet
    @DebugInterceptor
    fun provideChuckerInterceptor(@ApplicationContext context: Context): Interceptor {
        val chucker = ChuckerInterceptor.Builder(context)
            .collector(
                ChuckerCollector(
                    context = context,
                    showNotification = true,
                    retentionPeriod = RetentionManager.Period.ONE_DAY,
                ),
            )
            .alwaysReadResponseBody(true)
            .build()
        return SkipSensitiveCallsInterceptor(chucker)
    }

    /** Opened from the drawer, so Chucker is reachable even when notifications are blocked. */
    @Provides
    fun provideNetworkInspector(): NetworkInspector = NetworkInspector(Chucker::getLaunchIntent)

    /** OkHttp Profiler: streams calls to the Android Studio "OkHttp Profiler" plugin. */
    @Provides
    @IntoSet
    @DebugInterceptor
    fun provideOkHttpProfilerInterceptor(): Interceptor =
        SkipSensitiveCallsInterceptor(OkHttpProfilerInterceptor())
}
