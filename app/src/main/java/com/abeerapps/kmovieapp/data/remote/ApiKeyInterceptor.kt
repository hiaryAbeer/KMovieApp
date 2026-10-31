package com.abeerapps.kmovieapp.data.remote

import com.abeerapps.kmovieapp.data.remote.TmdbEndpoints.QueryParams
import okhttp3.Interceptor
import okhttp3.Response

/** Appends the TMDB `api_key` query parameter to every request. */
class ApiKeyInterceptor(private val apiKey: String) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val url = request.url.newBuilder()
            .addQueryParameter(QueryParams.API_KEY, apiKey)
            .build()
        return chain.proceed(request.newBuilder().url(url).build())
    }
}
