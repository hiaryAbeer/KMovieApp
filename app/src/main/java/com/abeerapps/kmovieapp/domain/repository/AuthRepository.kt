package com.abeerapps.kmovieapp.domain.repository

import com.abeerapps.kmovieapp.domain.model.Outcome
import com.abeerapps.kmovieapp.domain.model.Session
import kotlinx.coroutines.flow.Flow

/**
 * TMDB sign-in. Either way a request token is approved and exchanged for a session:
 * - website: the user approves it on themoviedb.org, which redirects back to the app;
 * - credentials: the app approves it with the user's username and password.
 */
interface AuthRepository {
    /** The signed-in user, or null when signed out. */
    val session: Flow<Session?>

    /** Creates a request token and returns the TMDB page where the user approves it. */
    suspend fun createSignInUrl(): Outcome<String>

    /** Exchanges an approved request token for a session and stores it. */
    suspend fun completeSignIn(requestToken: String): Outcome<Session>

    /** Signs in with TMDB credentials. The password is sent to TMDB and never stored. */
    suspend fun signInWithCredentials(username: String, password: String): Outcome<Session>

    suspend fun signOut()
}
