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
    } catch (e: Exception) {
        Outcome.Failure(e.toDataError())
    }

/** Maps API exceptions to [DataError]; rethrows anything unexpected so real bugs still crash. */
fun Exception.toDataError(): DataError = when (this) {
    is IOException -> DataError.NETWORK.also { Timber.w(this, "Network error") }
    is HttpException -> when (code()) {
        401 -> DataError.UNAUTHORIZED
        else -> DataError.SERVER
    }.also { Timber.w(this, "HTTP %d", code()) }

    is SerializationException -> DataError.UNKNOWN.also { Timber.e(this, "Malformed response") }
    else -> throw this
}
