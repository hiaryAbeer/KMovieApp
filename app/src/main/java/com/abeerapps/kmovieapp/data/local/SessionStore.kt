package com.abeerapps.kmovieapp.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.abeerapps.kmovieapp.domain.model.Session
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.sessionDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "session",
)

/**
 * Persists the TMDB session in app-private storage. The file is excluded from backups and
 * device transfers (see res/xml/data_extraction_rules.xml), because the session_id is a credential.
 */
@Singleton
class SessionStore @Inject constructor(@ApplicationContext context: Context) {
    private val dataStore = context.sessionDataStore

    val session: Flow<Session?> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { prefs ->
            val sessionId = prefs[KEY_SESSION_ID] ?: return@map null
            val accountId = prefs[KEY_ACCOUNT_ID] ?: return@map null
            Session(sessionId, accountId, prefs[KEY_USERNAME].orEmpty())
        }
        .distinctUntilChanged()

    suspend fun current(): Session? = session.first()

    suspend fun save(session: Session) {
        dataStore.edit { prefs ->
            prefs[KEY_SESSION_ID] = session.sessionId
            prefs[KEY_ACCOUNT_ID] = session.accountId
            prefs[KEY_USERNAME] = session.username
        }
    }

    suspend fun clear() {
        dataStore.edit { it.clear() }
    }

    /** The request token this app sent to TMDB for approval; only its redirect is accepted. */
    suspend fun savePendingRequestToken(token: String) {
        dataStore.edit { it[KEY_PENDING_REQUEST_TOKEN] = token }
    }

    /** Returns the pending request token and forgets it, so each token can be used once. */
    suspend fun consumePendingRequestToken(): String? {
        var token: String? = null
        dataStore.edit { prefs ->
            token = prefs[KEY_PENDING_REQUEST_TOKEN]
            prefs.remove(KEY_PENDING_REQUEST_TOKEN)
        }
        return token
    }

    private companion object {
        val KEY_SESSION_ID = stringPreferencesKey("session_id")
        val KEY_ACCOUNT_ID = longPreferencesKey("account_id")
        val KEY_USERNAME = stringPreferencesKey("username")
        val KEY_PENDING_REQUEST_TOKEN = stringPreferencesKey("pending_request_token")
    }
}
