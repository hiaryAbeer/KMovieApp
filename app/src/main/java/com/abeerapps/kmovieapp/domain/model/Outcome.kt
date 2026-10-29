package com.abeerapps.kmovieapp.domain.model

/** Result of a data operation. Failures carry a [DataError] instead of a raw exception. */
sealed interface Outcome<out T> {
    data class Success<T>(val data: T) : Outcome<T>
    data class Failure(val error: DataError) : Outcome<Nothing>
}

enum class DataError { NETWORK, SERVER, UNKNOWN }

fun <T> Outcome<T>.getOrNull(): T? = (this as? Outcome.Success)?.data
