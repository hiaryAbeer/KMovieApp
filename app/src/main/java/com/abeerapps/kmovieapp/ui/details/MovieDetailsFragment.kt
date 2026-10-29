package com.abeerapps.kmovieapp.ui.details

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.abeerapps.kmovieapp.R
import com.abeerapps.kmovieapp.databinding.FragmentMovieDetailsBinding
import com.abeerapps.kmovieapp.domain.model.MovieDetails
import com.abeerapps.kmovieapp.ui.common.BaseFragment
import com.abeerapps.kmovieapp.ui.common.TmdbImageSize
import com.abeerapps.kmovieapp.ui.common.applyStatusBarMargin
import com.abeerapps.kmovieapp.ui.common.applySystemBarsPadding
import com.abeerapps.kmovieapp.ui.common.collectWithViewLifecycle
import com.abeerapps.kmovieapp.ui.common.loadTmdbImage
import com.abeerapps.kmovieapp.ui.common.messageRes
import com.abeerapps.kmovieapp.ui.common.navigateToLogin
import com.abeerapps.kmovieapp.ui.common.render
import com.abeerapps.kmovieapp.ui.common.setOnRetryClickListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MovieDetailsFragment :
    BaseFragment<FragmentMovieDetailsBinding>(FragmentMovieDetailsBinding::inflate) {

    private val viewModel: MovieDetailsViewModel by viewModels()

    private val castAdapter = CastAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        applyInsets()

        binding.castList.adapter = castAdapter
        setUpClickListeners()
        observeViewModel()
    }

    /** The poster draws behind the status bar; the buttons on top of it stay below. */
    private fun applyInsets() = with(binding) {
        backButton.applyStatusBarMargin()
        favoriteButton.applyStatusBarMargin()
        scrollContent.applySystemBarsPadding(bottom = true)
    }

    private fun setUpClickListeners() = with(binding) {
        backButton.setOnClickListener { findNavController().navigateUp() }
        favoriteButton.setOnClickListener { viewModel.onFavoriteClick() }
        playTrailerButton.setOnClickListener { currentDetails()?.trailer?.let { openUrl(it.url) } }
        errorView.setOnRetryClickListener(viewModel::retry)
    }

    private fun observeViewModel() {
        collectWithViewLifecycle(viewModel.uiState, ::render)
        collectWithViewLifecycle(viewModel.favoriteState, ::renderFavorite)
        collectWithViewLifecycle(viewModel.events, ::handleEvent)
    }

    private fun render(state: MovieDetailsUiState) = with(binding) {
        val isLoading = state is MovieDetailsUiState.Loading
        val details = (state as? MovieDetailsUiState.Content)?.details

        shimmer.isVisible = isLoading
        if (isLoading) shimmer.startShimmer() else shimmer.stopShimmer()
        scrollContent.isVisible = details != null
        favoriteButton.isVisible = details != null
        playTrailerButton.isVisible = details?.trailer != null
        errorView.render((state as? MovieDetailsUiState.Error)?.error)

        details?.let(::renderDetails)
    }

    private fun renderDetails(details: MovieDetails) = with(binding) {
        poster.loadTmdbImage(details.posterPath ?: details.backdropPath, TmdbImageSize.POSTER_LARGE)
        poster.contentDescription = getString(R.string.cd_movie_poster, details.title)
        title.text = details.title
        tagline.text = details.tagline
        tagline.isVisible = details.tagline != null
        genres.text = details.genres.joinToString(SEPARATOR) { it.name }
        genres.isVisible = details.genres.isNotEmpty()
        rating.text = getString(R.string.rating_format, details.rating)
        meta.text = buildMetaLine(details)
        overview.text = details.overview.ifBlank { getString(R.string.no_overview) }

        castAdapter.submitList(details.cast)
        castTitle.isVisible = details.cast.isNotEmpty()
        castList.isVisible = details.cast.isNotEmpty()
    }

    private fun renderFavorite(state: FavoriteUiState) = with(binding) {
        favoriteIcon.setImageResource(
            if (state.isFavorite) R.drawable.ic_favorite else R.drawable.ic_favorite_border,
        )
        favoriteButton.contentDescription = getString(
            if (state.isFavorite) R.string.cd_remove_favorite else R.string.cd_add_favorite,
        )
        favoriteButton.isEnabled = !state.isUpdating
    }

    private fun handleEvent(event: MovieDetailsEvent) {
        when (event) {
            MovieDetailsEvent.RequireSignIn -> navigateToLogin()
            is MovieDetailsEvent.ShowError -> showMessage(event.error.messageRes())
        }
    }

    private fun currentDetails(): MovieDetails? =
        (viewModel.uiState.value as? MovieDetailsUiState.Content)?.details

    /** "2025 • 2h 05m • 1,234 votes", skipping parts TMDB doesn't have. */
    private fun buildMetaLine(details: MovieDetails): String = listOfNotNull(
        details.releaseDate?.take(4),
        details.runtimeMinutes?.let { getString(R.string.runtime_format, it / 60, it % 60) },
        resources.getQuantityString(R.plurals.vote_count, details.voteCount, details.voteCount),
    ).joinToString(SEPARATOR)

    private fun openUrl(url: String) {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, url.toUri()))
        } catch (_: ActivityNotFoundException) {
            showMessage(R.string.error_no_app_to_open_link)
        }
    }

    override fun onDestroyView() {
        binding.castList.adapter = null
        super.onDestroyView()
    }

    private companion object {
        const val SEPARATOR = " • "
    }
}
