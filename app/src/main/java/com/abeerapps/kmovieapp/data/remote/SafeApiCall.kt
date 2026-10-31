package com.abeerapps.kmovieapp.data.remote

import com.abeerapps.kmovieapp.domain.model.DataError
import com.abeerapps.kmovieapp.domain.model.Outcome
import java.io.IOException
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.SerializationException
import retrofit2.HttpException
import timber.log.Timber

/** Runs [block] and maps failures to [DataError], never swallowing coroutine cancellation. */
// This is the boundary where every failure becomes a DataError; toDataError() rethrows
// anything that isn't a network/HTTP/parsing error, so real bugs still crash.
@Suppress("TooGenericExceptionCaught")
suspend fun <T> safeApiCall(block: suspend () -> T): Outcome<T> = try {
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
        HTTP_UNAUTHORIZED -> DataError.UNAUTHORIZED
        else -> DataError.SERVER
    }.also { Timber.w(this, "HTTP %d", code()) }

    is SerializationException -> DataError.UNKNOWN.also { Timber.e(this, "Malformed response") }

    else -> throw this
}

private const val HTTP_UNAUTHORIZED = 401
