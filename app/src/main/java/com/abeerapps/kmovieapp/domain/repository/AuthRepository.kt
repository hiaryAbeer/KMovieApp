package com.abeerapps.kmovieapp.domain.repository

import com.abeerapps.kmovieapp.domain.model.Outcome
import com.abeerapps.kmovieapp.domain.model.Session
import kotlinx.coroutines.flow.Flow

/**
 * TMDB sign-in: the user approves a request token on TMDB's website, which redirects
 * back to the app, and the approved token is exchanged for a session.
 */
interface AuthRepository {
    /** The signed-in user, or null when signed out. */
    val session: Flow<Session?>

    /** Creates a request token and returns the TMDB page where the user approves it. */
    suspend fun createSignInUrl(): Outcome<String>

    /** Exchanges an approved request token for a session and stores it. */
    suspend fun completeSignIn(requestToken: String): Outcome<Session>

    suspend fun signOut()
}
