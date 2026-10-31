package com.abeerapps.kmovieapp.domain.model

/** A signed-in TMDB user. */
data class Session(val sessionId: String, val accountId: Long, val username: String)
