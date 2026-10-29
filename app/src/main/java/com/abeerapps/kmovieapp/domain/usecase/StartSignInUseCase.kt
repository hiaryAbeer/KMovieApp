package com.abeerapps.kmovieapp.domain.usecase

import com.abeerapps.kmovieapp.domain.repository.AuthRepository
import javax.inject.Inject

/** Returns the TMDB page where the user approves this app. */
class StartSignInUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke() = authRepository.createSignInUrl()
}
