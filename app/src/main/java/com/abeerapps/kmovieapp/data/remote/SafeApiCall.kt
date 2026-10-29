package com.abeerapps.kmovieapp.data.remote

import com.abeerapps.kmovieapp.domain.model.DataError
import com.abeerapps.kmovieapp.domain.model.Outcome
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.SerializationException
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException

/** Runs [block] and maps failures to [DataError], never swallowing coroutine cancellation. */
suspend fun <T> safeApiCall(block: suspend () -> T): Outcome<T> =
    try {
        Outcome.Success(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: IOException) {
        Timber.w(e, "Network error")
        Outcome.Failure(DataError.NETWORK)
    } catch (e: HttpException) {
        Timber.w(e, "HTTP %d", e.code())
        Outcome.Failure(DataError.SERVER)
    } catch (e: SerializationException) {
        Timber.e(e, "Malformed response")
        Outcome.Failure(DataError.UNKNOWN)
    }
