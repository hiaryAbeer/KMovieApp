package com.abeerapps.kmovieapp.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RequestTokenDto(
    @SerialName("request_token") val requestToken: String,
)

@Serializable
data class CreateSessionRequestDto(
    @SerialName("request_token") val requestToken: String,
)

/** Approves a request token with the user's TMDB credentials, without the website. */
@Serializable
data class ValidateWithLoginRequestDto(
    val username: String,
    val password: String,
    @SerialName("request_token") val requestToken: String,
) {
    // Keep the password out of logs and crash reports.
    override fun toString() = "ValidateWithLoginRequestDto(username=$username, password=***)"
}

@Serializable
data class SessionDto(
    @SerialName("session_id") val sessionId: String,
)

@Serializable
data class DeleteSessionRequestDto(
    @SerialName("session_id") val sessionId: String,
)

@Serializable
data class AccountDto(
    val id: Long,
    val username: String = "",
)

/** Request body for marking a movie as favorite. No defaults, so every field is serialized. */
@Serializable
data class FavoriteRequestDto(
    @SerialName("media_type") val mediaType: String,
    @SerialName("media_id") val mediaId: Long,
    val favorite: Boolean,
)

@Serializable
data class StatusDto(
    @SerialName("status_code") val statusCode: Int = 0,
    @SerialName("status_message") val statusMessage: String = "",
)

@Serializable
data class AccountStatesDto(
    val id: Long,
    val favorite: Boolean = false,
)
