package com.abeerapps.kmovieapp.domain.usecase

import com.abeerapps.kmovieapp.domain.repository.AuthRepository
import javax.inject.Inject

class SignInWithCredentialsUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    /** Usernames are trimmed; passwords are sent exactly as typed. */
    suspend operator fun invoke(username: String, password: String) =
        authRepository.signInWithCredentials(username.trim(), password)
}
