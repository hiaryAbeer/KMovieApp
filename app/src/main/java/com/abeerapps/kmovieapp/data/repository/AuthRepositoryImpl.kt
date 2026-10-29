package com.abeerapps.kmovieapp.data.repository

import com.abeerapps.kmovieapp.BuildConfig
import com.abeerapps.kmovieapp.data.local.SessionStore
import com.abeerapps.kmovieapp.data.remote.TmdbApi
import com.abeerapps.kmovieapp.data.remote.dto.CreateSessionRequestDto
import com.abeerapps.kmovieapp.data.remote.dto.DeleteSessionRequestDto
import com.abeerapps.kmovieapp.data.remote.safeApiCall
import com.abeerapps.kmovieapp.domain.model.DataError
import com.abeerapps.kmovieapp.domain.model.Outcome
import com.abeerapps.kmovieapp.domain.model.Session
import com.abeerapps.kmovieapp.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import timber.log.Timber
import java.net.URLEncoder
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val api: TmdbApi,
    private val sessionStore: SessionStore,
) : AuthRepository {

    override val session: Flow<Session?> = sessionStore.session

    override suspend fun createSignInUrl(): Outcome<String> = safeApiCall {
        val token = api.createRequestToken().requestToken
        sessionStore.savePendingRequestToken(token)
        "$APPROVE_URL$token?redirect_to=${URLEncoder.encode(REDIRECT_URI, Charsets.UTF_8.name())}"
    }

    override suspend fun completeSignIn(requestToken: String): Outcome<Session> {
        // The redirect is a deep link any app can send. Accepting a token we didn't issue would
        // let an attacker sign the user into the attacker's account (login CSRF).
        if (sessionStore.consumePendingRequestToken() != requestToken) {
            Timber.w("Ignoring sign-in redirect with an unexpected request token")
            return Outcome.Failure(DataError.UNAUTHORIZED)
        }
        return createSession(requestToken)
    }

    private suspend fun createSession(requestToken: String): Outcome<Session> = safeApiCall {
        val sessionId = api.createSession(CreateSessionRequestDto(requestToken)).sessionId
        val account = api.getAccount(sessionId)
        Session(sessionId, account.id, account.username).also { sessionStore.save(it) }
    }

    override suspend fun signOut() {
        val session = sessionStore.current() ?: return
        // Sign out locally even if TMDB can't be reached; the session just expires server-side.
        safeApiCall { api.deleteSession(DeleteSessionRequestDto(session.sessionId)) }
        sessionStore.clear()
    }

    private companion object {
        const val APPROVE_URL = "https://www.themoviedb.org/authenticate/"
        const val REDIRECT_URI = BuildConfig.TMDB_AUTH_REDIRECT_URI
    }
}
