package com.abeerapps.kmovieapp.di

import javax.inject.Qualifier

/**
 * OkHttp interceptors that only exist in debug builds (network inspectors).
 * Contributed from the `debug` source set; the set is empty in release.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DebugInterceptor
