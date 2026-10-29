package com.abeerapps.kmovieapp.ui.common

import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.abeerapps.kmovieapp.MainNavGraphDirections
import com.abeerapps.kmovieapp.R

/** Opens movie details, ignoring repeated taps that arrive after navigation already happened. */
fun Fragment.navigateToMovieDetails(movieId: Long) {
    val navController = findNavController()
    if (navController.currentDestination?.id == R.id.movieDetailsFragment) return
    navController.navigate(MainNavGraphDirections.actionGlobalMovieDetails(movieId))
}
