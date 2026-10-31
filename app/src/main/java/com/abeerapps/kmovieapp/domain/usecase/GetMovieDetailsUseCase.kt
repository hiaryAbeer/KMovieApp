package com.abeerapps.kmovieapp.domain.usecase

import com.abeerapps.kmovieapp.domain.model.MovieDetails
import com.abeerapps.kmovieapp.domain.model.Outcome
import com.abeerapps.kmovieapp.domain.model.getOrNull
import com.abeerapps.kmovieapp.domain.repository.MovieRepository
import javax.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

/**
 * Loads a movie's details together with its cast and trailer, in parallel.
 * Cast and trailer are optional: if either request fails the screen still shows the details.
 */
class GetMovieDetailsUseCase @Inject constructor(private val repository: MovieRepository) {
    suspend operator fun invoke(movieId: Long): Outcome<MovieDetails> = coroutineScope {
        val cast = async { repository.getMovieCast(movieId) }
        val trailer = async { repository.getMovieTrailer(movieId) }

        when (val details = repository.getMovieDetails(movieId)) {
            is Outcome.Success -> Outcome.Success(
                details.data.copy(
                    cast = cast.await().getOrNull().orEmpty(),
                    trailer = trailer.await().getOrNull(),
                ),
            )

            is Outcome.Failure -> {
                cast.cancel()
                trailer.cancel()
                details
            }
        }
    }
}
