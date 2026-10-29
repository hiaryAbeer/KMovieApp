package com.abeerapps.kmovieapp.ui

import app.cash.turbine.test
import com.abeerapps.kmovieapp.domain.model.DataError
import com.abeerapps.kmovieapp.domain.model.Outcome
import com.abeerapps.kmovieapp.domain.model.Session
import com.abeerapps.kmovieapp.domain.repository.AuthRepository
import com.abeerapps.kmovieapp.testing.MainDispatcherRule
import com.abeerapps.kmovieapp.ui.auth.SignInEvent
import com.abeerapps.kmovieapp.ui.auth.SignInViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class SignInViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val authRepository = mockk<AuthRepository> { every { session } returns flowOf(null) }
    private val viewModel = SignInViewModel(authRepository)

    @Test
    fun `start opens TMDB's approval page`() = runTest {
        coEvery { authRepository.createSignInUrl() } returns Outcome.Success("https://tmdb/approve")

        viewModel.events.test {
            viewModel.startSignIn()
            assertEquals(SignInEvent.OpenApprovalPage("https://tmdb/approve"), awaitItem())
        }
    }

    @Test
    fun `approved token completes sign-in`() = runTest {
        coEvery { authRepository.completeSignIn("token") } returns
            Outcome.Success(Session("session", 1L, "abeer"))

        viewModel.events.test {
            viewModel.onApprovalResult(requestToken = "token", approved = true)
            assertEquals(SignInEvent.SignedIn("abeer"), awaitItem())
        }
    }

    @Test
    fun `denied approval is reported as cancelled without creating a session`() = runTest {
        viewModel.events.test {
            viewModel.onApprovalResult(requestToken = "token", approved = false)
            assertEquals(SignInEvent.Cancelled, awaitItem())
        }
        coVerify(exactly = 0) { authRepository.completeSignIn(any()) }
    }

    @Test
    fun `failed session creation is reported`() = runTest {
        coEvery { authRepository.completeSignIn("token") } returns Outcome.Failure(DataError.SERVER)

        viewModel.events.test {
            viewModel.onApprovalResult(requestToken = "token", approved = true)
            assertEquals(SignInEvent.Failed(DataError.SERVER), awaitItem())
        }
    }
}
