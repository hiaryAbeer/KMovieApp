package com.abeerapps.kmovieapp.ui

import app.cash.turbine.test
import com.abeerapps.kmovieapp.domain.model.DataError
import com.abeerapps.kmovieapp.domain.model.Outcome
import com.abeerapps.kmovieapp.domain.model.Session
import com.abeerapps.kmovieapp.domain.repository.AuthRepository
import com.abeerapps.kmovieapp.domain.usecase.SignInWithCredentialsUseCase
import com.abeerapps.kmovieapp.testing.MainDispatcherRule
import com.abeerapps.kmovieapp.ui.login.LoginViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class LoginViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val authRepository = mockk<AuthRepository>()
    private val viewModel = LoginViewModel(SignInWithCredentialsUseCase(authRepository))

    private fun fillForm(username: String = "abeer", password: String = "secret") {
        viewModel.onUsernameChange(username)
        viewModel.onPasswordChange(password)
    }

    @Test
    fun `cannot submit until both fields are filled`() {
        assertFalse(viewModel.uiState.value.canSubmit)
        viewModel.onUsernameChange("abeer")
        assertFalse(viewModel.uiState.value.canSubmit)
        viewModel.onPasswordChange("secret")
        assertTrue(viewModel.uiState.value.canSubmit)
    }

    @Test
    fun `successful sign-in clears the password and announces the session`() = runTest {
        coEvery { authRepository.signInWithCredentials("abeer", "secret") } returns
            Outcome.Success(SESSION)
        fillForm()

        viewModel.signedIn.test {
            viewModel.onSubmit()
            assertEquals(SESSION, awaitItem())
        }
        assertEquals("", viewModel.uiState.value.password)
        assertFalse(viewModel.uiState.value.isSubmitting)
    }

    @Test
    fun `username is trimmed and password sent exactly as typed`() {
        coEvery { authRepository.signInWithCredentials(any(), any()) } returns Outcome.Success(SESSION)
        fillForm(username = "  abeer ", password = " pass word ")

        viewModel.onSubmit()

        coVerify { authRepository.signInWithCredentials("abeer", " pass word ") }
    }

    @Test
    fun `rejected credentials keep the form and show the error`() {
        coEvery { authRepository.signInWithCredentials(any(), any()) } returns
            Outcome.Failure(DataError.UNAUTHORIZED)
        fillForm()

        viewModel.onSubmit()

        val state = viewModel.uiState.value
        assertEquals(DataError.UNAUTHORIZED, state.error)
        assertEquals("secret", state.password)
        assertFalse(state.isSubmitting)
    }

    @Test
    fun `editing a field clears the previous error`() {
        coEvery { authRepository.signInWithCredentials(any(), any()) } returns
            Outcome.Failure(DataError.UNAUTHORIZED)
        fillForm()
        viewModel.onSubmit()

        viewModel.onPasswordChange("other")

        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun `submitting twice while a request is running sends one request`() {
        val pending = CompletableDeferred<Outcome<Session>>()
        coEvery { authRepository.signInWithCredentials(any(), any()) } coAnswers { pending.await() }
        fillForm()

        viewModel.onSubmit()
        viewModel.onSubmit()

        coVerify(exactly = 1) { authRepository.signInWithCredentials(any(), any()) }
        pending.complete(Outcome.Success(SESSION))
    }

    private companion object {
        val SESSION = Session(sessionId = "session", accountId = 1L, username = "abeer")
    }
}
