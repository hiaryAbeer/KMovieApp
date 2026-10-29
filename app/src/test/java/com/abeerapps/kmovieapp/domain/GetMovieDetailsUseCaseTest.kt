package com.abeerapps.kmovieapp.domain

import com.abeerapps.kmovieapp.domain.model.DataError
import com.abeerapps.kmovieapp.domain.model.Outcome
import com.abeerapps.kmovieapp.domain.repository.MovieRepository
import com.abeerapps.kmovieapp.domain.usecase.GetMovieDetailsUseCase
import com.abeerapps.kmovieapp.testing.movieDetails
import com.abeerapps.kmovieapp.testing.sampleCast
import com.abeerapps.kmovieapp.testing.sampleTrailer
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetMovieDetailsUseCaseTest {

    private val repository = mockk<MovieRepository>()
    private val useCase = GetMovieDetailsUseCase(repository)

    @Test
    fun `combines details, cast and trailer`() = runTest {
        coEvery { repository.getMovieDetails(1L) } returns Outcome.Success(movieDetails())
        coEvery { repository.getMovieCast(1L) } returns Outcome.Success(sampleCast)
        coEvery { repository.getMovieTrailer(1L) } returns Outcome.Success(sampleTrailer)

        val result = useCase(1L)

        assertEquals(
            Outcome.Success(movieDetails().copy(cast = sampleCast, trailer = sampleTrailer)),
            result,
        )
    }

    @Test
    fun `still returns details when cast and trailer fail`() = runTest {
        coEvery { repository.getMovieDetails(1L) } returns Outcome.Success(movieDetails())
        coEvery { repository.getMovieCast(1L) } returns Outcome.Failure(DataError.SERVER)
        coEvery { repository.getMovieTrailer(1L) } returns Outcome.Failure(DataError.NETWORK)

        val result = useCase(1L)

        assertEquals(Outcome.Success(movieDetails()), result)
    }

    @Test
    fun `fails when details fail`() = runTest {
        coEvery { repository.getMovieDetails(1L) } returns Outcome.Failure(DataError.NETWORK)
        coEvery { repository.getMovieCast(1L) } returns Outcome.Success(sampleCast)
        coEvery { repository.getMovieTrailer(1L) } returns Outcome.Success(sampleTrailer)

        assertEquals(Outcome.Failure(DataError.NETWORK), useCase(1L))
    }
}
