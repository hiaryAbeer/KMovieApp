package com.abeerapps.kmovieapp.di

import com.abeerapps.kmovieapp.ui.debug.NetworkInspector
import dagger.BindsOptionalOf
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/** Debug tools are optional: bound in the debug source set, absent in release. */
@Module
@InstallIn(SingletonComponent::class)
interface DebugToolsModule {
    @BindsOptionalOf
    fun networkInspector(): NetworkInspector
}
