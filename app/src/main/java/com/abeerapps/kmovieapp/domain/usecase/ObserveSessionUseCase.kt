package com.abeerapps.kmovieapp.domain.usecase

import com.abeerapps.kmovieapp.domain.repository.AuthRepository
import javax.inject.Inject

/** The signed-in TMDB user, or null when signed out. */
class ObserveSessionUseCase @Inject constructor(private val authRepository: AuthRepository) {
    operator fun invoke() = authRepository.session
}
