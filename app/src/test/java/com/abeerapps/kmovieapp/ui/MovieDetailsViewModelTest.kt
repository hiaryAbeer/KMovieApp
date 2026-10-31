package com.abeerapps.kmovieapp.ui

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.abeerapps.kmovieapp.domain.model.DataError
import com.abeerapps.kmovieapp.domain.model.Outcome
import com.abeerapps.kmovieapp.domain.model.Session
import com.abeerapps.kmovieapp.domain.repository.AuthRepository
import com.abeerapps.kmovieapp.domain.repository.FavoritesRepository
import com.abeerapps.kmovieapp.domain.usecase.GetMovieDetailsUseCase
import com.abeerapps.kmovieapp.domain.usecase.IsFavoriteUseCase
import com.abeerapps.kmovieapp.domain.usecase.ObserveSessionUseCase
import com.abeerapps.kmovieapp.domain.usecase.SetFavoriteUseCase
import com.abeerapps.kmovieapp.testing.MainDispatcherRule
import com.abeerapps.kmovieapp.testing.movieDetails
import com.abeerapps.kmovieapp.ui.details.FavoriteUiState
import com.abeerapps.kmovieapp.ui.details.MovieDetailsEvent
import com.abeerapps.kmovieapp.ui.details.MovieDetailsUiState
import com.abeerapps.kmovieapp.ui.details.MovieDetailsViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MovieDetailsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val session = MutableStateFlow<Session?>(null)
    private val getMovieDetails = mockk<GetMovieDetailsUseCase>()
    private val favoritesRepository = mockk<FavoritesRepository>()
    private val authRepository = mockk<AuthRepository> {
        every { session } returns this@MovieDetailsViewModelTest.session
    }

    @Before
    fun setUp() {
        coEvery { getMovieDetails(MOVIE_ID) } returns Outcome.Success(movieDetails(MOVIE_ID))
        coEvery { favoritesRepository.isFavorite(MOVIE_ID) } returns Outcome.Success(false)
        coEvery { favoritesRepository.setFavorite(MOVIE_ID, any()) } returns Outcome.Success(Unit)
    }

    private fun createViewModel() = MovieDetailsViewModel(
        SavedStateHandle(mapOf("movieId" to MOVIE_ID)),
        getMovieDetails,
        IsFavoriteUseCase(favoritesRepository),
        SetFavoriteUseCase(favoritesRepository),
        ObserveSessionUseCase(authRepository),
    )

    @Test
    fun `loads details for the movie in the navigation arguments`() {
        val viewModel = createViewModel()

        assertEquals(MovieDetailsUiState.Content(movieDetails(MOVIE_ID)), viewModel.uiState.value)
    }

    @Test
    fun `tapping the heart while signed out asks the user to sign in`() = runTest {
        val viewModel = createViewModel()

        viewModel.events.test {
            viewModel.onFavoriteClick()
            assertEquals(MovieDetailsEvent.RequireSignIn, awaitItem())
        }
        coVerify(exactly = 0) { favoritesRepository.setFavorite(any(), any()) }
    }

    @Test
    fun `favorites the movie once the user finishes signing in`() {
        val viewModel = createViewModel()
        viewModel.onFavoriteClick()

        session.value = SESSION

        coVerify { favoritesRepository.setFavorite(MOVIE_ID, true) }
        assertEquals(FavoriteUiState(isSignedIn = true, isFavorite = true), viewModel.favoriteState.value)
    }

    @Test
    fun `signed in user toggles favorite`() {
        session.value = SESSION
        val viewModel = createViewModel()

        viewModel.onFavoriteClick()

        coVerify { favoritesRepository.setFavorite(MOVIE_ID, true) }
        assertEquals(FavoriteUiState(isSignedIn = true, isFavorite = true), viewModel.favoriteState.value)
    }

    @Test
    fun `failed toggle rolls back and reports the error`() = runTest {
        session.value = SESSION
        coEvery { favoritesRepository.setFavorite(MOVIE_ID, true) } returns Outcome.Failure(DataError.NETWORK)
        val viewModel = createViewModel()

        viewModel.events.test {
            viewModel.onFavoriteClick()
            assertEquals(MovieDetailsEvent.ShowError(DataError.NETWORK), awaitItem())
        }
        assertEquals(FavoriteUiState(isSignedIn = true, isFavorite = false), viewModel.favoriteState.value)
    }

    private companion object {
        const val MOVIE_ID = 42L
        val SESSION = Session(sessionId = "session", accountId = 1L, username = "abeer")
    }
}
