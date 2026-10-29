package com.abeerapps.kmovieapp.ui.common

import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.abeerapps.kmovieapp.MainNavGraphDirections
import com.abeerapps.kmovieapp.R

/**
 * Navigates only if [directions] is valid from the current destination, so a quick double tap
 * doesn't crash after the first tap has already navigated away.
 */
fun Fragment.navigateSafely(directions: NavDirections) {
    val navController = findNavController()
    if (navController.currentDestination?.getAction(directions.actionId) != null) {
        navController.navigate(directions)
    }
}

/** Opens the login screen from anywhere, unless it is already showing. */
fun Fragment.navigateToLogin() {
    val navController = findNavController()
    if (navController.currentDestination?.id == R.id.loginFragment) return
    navController.navigate(MainNavGraphDirections.actionGlobalLogin())
}

/** Opens movie details; a global action, so the destination itself is checked instead. */
fun Fragment.navigateToMovieDetails(movieId: Long) {
    val navController = findNavController()
    if (navController.currentDestination?.id == R.id.movieDetailsFragment) return
    navController.navigate(MainNavGraphDirections.actionGlobalMovieDetails(movieId))
}
