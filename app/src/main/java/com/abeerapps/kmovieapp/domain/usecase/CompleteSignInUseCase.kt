package com.abeerapps.kmovieapp.domain.usecase

import com.abeerapps.kmovieapp.domain.repository.AuthRepository
import javax.inject.Inject

class CompleteSignInUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(requestToken: String) = authRepository.completeSignIn(requestToken)
}
