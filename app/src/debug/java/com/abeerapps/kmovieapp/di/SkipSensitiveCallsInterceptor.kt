package com.abeerapps.kmovieapp.di

import okhttp3.Interceptor
import okhttp3.Response

/**
 * Wraps a network inspector so calls that carry credentials pass straight through,
 * keeping the user's password out of Chucker and OkHttp Profiler recordings.
 */
class SkipSensitiveCallsInterceptor(
    private val inspector: Interceptor,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val path = chain.request().url.encodedPath
        return if (SENSITIVE_PATHS.any(path::endsWith)) {
            chain.proceed(chain.request())
        } else {
            inspector.intercept(chain)
        }
    }

    private companion object {
        val SENSITIVE_PATHS = listOf("/authentication/token/validate_with_login")
    }
}
