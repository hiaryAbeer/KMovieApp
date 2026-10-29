package com.abeerapps.kmovieapp.data

import com.abeerapps.kmovieapp.data.local.SessionStore
import com.abeerapps.kmovieapp.data.remote.TmdbApi
import com.abeerapps.kmovieapp.data.remote.dto.AccountDto
import com.abeerapps.kmovieapp.data.remote.dto.CreateSessionRequestDto
import com.abeerapps.kmovieapp.data.remote.dto.RequestTokenDto
import com.abeerapps.kmovieapp.data.remote.dto.SessionDto
import com.abeerapps.kmovieapp.data.remote.dto.ValidateWithLoginRequestDto
import com.abeerapps.kmovieapp.data.repository.AuthRepositoryImpl
import com.abeerapps.kmovieapp.domain.model.DataError
import com.abeerapps.kmovieapp.domain.model.Outcome
import com.abeerapps.kmovieapp.domain.model.Session
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

class AuthRepositoryImplTest {

    private val api = mockk<TmdbApi>()
    private val sessionStore = mockk<SessionStore>(relaxUnitFun = true) {
        every { session } returns flowOf(null)
    }
    private val repository = AuthRepositoryImpl(api, sessionStore)

    @Test
    fun `sign-in URL remembers the request token it sends for approval`() = runTest {
        coEvery { api.createRequestToken() } returns RequestTokenDto("issued")

        val result = repository.createSignInUrl()

        assertTrue((result as Outcome.Success).data.contains("/authenticate/issued?redirect_to="))
        coVerify { sessionStore.savePendingRequestToken("issued") }
    }

    @Test
    fun `redirect with the issued token creates and stores a session`() = runTest {
        coEvery { sessionStore.consumePendingRequestToken() } returns "issued"
        coEvery { api.createSession(CreateSessionRequestDto("issued")) } returns SessionDto("session")
        coEvery { api.getAccount("session") } returns AccountDto(id = 7L, username = "abeer")
        coEvery { sessionStore.save(any()) } just runs

        val result = repository.completeSignIn("issued")

        val expected = Session(sessionId = "session", accountId = 7L, username = "abeer")
        assertEquals(Outcome.Success(expected), result)
        coVerify { sessionStore.save(expected) }
    }

    @Test
    fun `redirect with a token the app did not issue is rejected (login CSRF)`() = runTest {
        coEvery { sessionStore.consumePendingRequestToken() } returns "issued"

        val result = repository.completeSignIn("attacker-token")

        assertEquals(Outcome.Failure(DataError.UNAUTHORIZED), result)
        coVerify(exactly = 0) { api.createSession(any()) }
    }

    @Test
    fun `credentials approve a fresh token, then create and store a session`() = runTest {
        coEvery { api.createRequestToken() } returns RequestTokenDto("fresh")
        coEvery {
            api.validateWithLogin(ValidateWithLoginRequestDto("abeer", "secret", "fresh"))
        } returns RequestTokenDto("fresh")
        coEvery { api.createSession(CreateSessionRequestDto("fresh")) } returns SessionDto("session")
        coEvery { api.getAccount("session") } returns AccountDto(id = 7L, username = "abeer")

        val result = repository.signInWithCredentials("abeer", "secret")

        val expected = Session(sessionId = "session", accountId = 7L, username = "abeer")
        assertEquals(Outcome.Success(expected), result)
        coVerify { sessionStore.save(expected) }
    }

    @Test
    fun `rejected credentials never create a session`() = runTest {
        coEvery { api.createRequestToken() } returns RequestTokenDto("fresh")
        coEvery { api.validateWithLogin(any()) } throws unauthorized()

        val result = repository.signInWithCredentials("abeer", "wrong")

        assertEquals(Outcome.Failure(DataError.UNAUTHORIZED), result)
        coVerify(exactly = 0) { api.createSession(any()) }
    }

    @Test
    fun `the credentials request never prints the password`() {
        val request = ValidateWithLoginRequestDto("abeer", "secret", "token")

        assertFalse(request.toString().contains("secret"))
    }

    private fun unauthorized() = HttpException(
        Response.error<Any>(401, "{}".toResponseBody("application/json".toMediaType())),
    )

    @Test
    fun `redirect without a pending sign-in is rejected`() = runTest {
        coEvery { sessionStore.consumePendingRequestToken() } returns null

        assertEquals(Outcome.Failure(DataError.UNAUTHORIZED), repository.completeSignIn("any"))
        coVerify(exactly = 0) { api.createSession(any()) }
    }
}
