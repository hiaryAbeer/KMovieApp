package com.abeerapps.kmovieapp.ui

import com.abeerapps.kmovieapp.domain.model.DataError
import com.abeerapps.kmovieapp.domain.model.Outcome
import com.abeerapps.kmovieapp.domain.usecase.GetGenresUseCase
import com.abeerapps.kmovieapp.domain.usecase.GetTrendingMoviesUseCase
import com.abeerapps.kmovieapp.testing.MainDispatcherRule
import com.abeerapps.kmovieapp.testing.genre
import com.abeerapps.kmovieapp.testing.movie
import com.abeerapps.kmovieapp.ui.home.HomeUiState
import com.abeerapps.kmovieapp.ui.home.HomeViewModel
import io.mockk.coEvery
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class HomeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val getTrendingMovies = mockk<GetTrendingMoviesUseCase>()
    private val getGenres = mockk<GetGenresUseCase>()

    @Test
    fun `loads trending movies and genres`() {
        coEvery { getTrendingMovies() } returns Outcome.Success(listOf(movie(1), movie(2)))
        coEvery { getGenres() } returns Outcome.Success(listOf(genre()))

        val viewModel = HomeViewModel(getTrendingMovies, getGenres)

        assertEquals(
            HomeUiState(
                isLoading = false,
                trending = listOf(movie(1), movie(2)),
                genres = listOf(genre()),
            ),
            viewModel.uiState.value,
        )
    }

    @Test
    fun `shows the screen without trending when only trending fails`() {
        coEvery { getTrendingMovies() } returns Outcome.Failure(DataError.SERVER)
        coEvery { getGenres() } returns Outcome.Success(listOf(genre()))

        val state = HomeViewModel(getTrendingMovies, getGenres).uiState.value

        assertEquals(HomeUiState(isLoading = false, genres = listOf(genre())), state)
    }

    @Test
    fun `exposes error when genres fail and recovers on retry`() {
        coEvery { getTrendingMovies() } returns Outcome.Success(listOf(movie()))
        coEvery { getGenres() } returns Outcome.Failure(DataError.NETWORK)
        val viewModel = HomeViewModel(getTrendingMovies, getGenres)

        assertEquals(DataError.NETWORK, viewModel.uiState.value.error)

        coEvery { getGenres() } returns Outcome.Success(listOf(genre()))
        viewModel.retry()

        assertEquals(null, viewModel.uiState.value.error)
        assertEquals(listOf(genre()), viewModel.uiState.value.genres)
    }
}
