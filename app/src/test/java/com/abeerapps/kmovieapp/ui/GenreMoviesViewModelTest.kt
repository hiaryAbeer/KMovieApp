package com.abeerapps.kmovieapp.ui

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.abeerapps.kmovieapp.domain.model.DataError
import com.abeerapps.kmovieapp.domain.model.Movie
import com.abeerapps.kmovieapp.domain.model.Outcome
import com.abeerapps.kmovieapp.domain.usecase.GetMoviesByGenreUseCase
import com.abeerapps.kmovieapp.testing.MainDispatcherRule
import com.abeerapps.kmovieapp.testing.movie
import com.abeerapps.kmovieapp.ui.genre.GenreMoviesFragment
import com.abeerapps.kmovieapp.ui.genre.GenreMoviesUiState
import com.abeerapps.kmovieapp.ui.genre.GenreMoviesViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class GenreMoviesViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val getMoviesByGenre = mockk<GetMoviesByGenreUseCase>()

    private fun createViewModel(genreId: Int = 28) = GenreMoviesViewModel(
        SavedStateHandle(mapOf(GenreMoviesFragment.ARG_GENRE_ID to genreId)),
        getMoviesByGenre,
    )

    @Test
    fun `loads movies for its own genre`() {
        coEvery { getMoviesByGenre(28) } returns Outcome.Success(listOf(movie(1)))
        coEvery { getMoviesByGenre(35) } returns Outcome.Success(listOf(movie(2)))

        val action = createViewModel(genreId = 28)
        val comedy = createViewModel(genreId = 35)

        assertEquals(listOf(movie(1)), action.uiState.value.movies)
        assertEquals(listOf(movie(2)), comedy.uiState.value.movies)
        coVerify(exactly = 1) { getMoviesByGenre(28) }
        coVerify(exactly = 1) { getMoviesByGenre(35) }
    }

    @Test
    fun `empty result is reported as empty`() {
        coEvery { getMoviesByGenre(28) } returns Outcome.Success(emptyList())

        assertTrue(createViewModel().uiState.value.isEmpty)
    }

    @Test
    fun `refresh keeps current movies visible while loading`() = runTest {
        coEvery { getMoviesByGenre(28) } returns Outcome.Success(listOf(movie(1)))
        val viewModel = createViewModel()
        val refreshResult = CompletableDeferred<Outcome<List<Movie>>>()
        coEvery { getMoviesByGenre(28) } coAnswers { refreshResult.await() }

        viewModel.uiState.test {
            assertEquals(GenreMoviesUiState(isLoading = false, movies = listOf(movie(1))), awaitItem())

            viewModel.refresh()
            assertEquals(
                GenreMoviesUiState(isLoading = false, isRefreshing = true, movies = listOf(movie(1))),
                awaitItem(),
            )

            refreshResult.complete(Outcome.Failure(DataError.NETWORK))
            assertEquals(
                GenreMoviesUiState(isLoading = false, movies = listOf(movie(1)), error = DataError.NETWORK),
                awaitItem(),
            )
        }
    }
}
