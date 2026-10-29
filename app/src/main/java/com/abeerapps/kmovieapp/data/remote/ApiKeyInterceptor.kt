package com.abeerapps.kmovieapp.data.remote

import okhttp3.Interceptor
import okhttp3.Response

/** Appends the TMDB `api_key` query parameter to every request. */
class ApiKeyInterceptor(private val apiKey: String) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val url = request.url.newBuilder()
            .addQueryParameter(QUERY_API_KEY, apiKey)
            .build()
        return chain.proceed(request.newBuilder().url(url).build())
    }

    companion object {
        const val QUERY_API_KEY = "api_key"
    }
}
