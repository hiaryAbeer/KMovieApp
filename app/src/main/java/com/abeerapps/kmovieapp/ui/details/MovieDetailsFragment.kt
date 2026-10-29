package com.abeerapps.kmovieapp.ui.details

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.abeerapps.kmovieapp.R
import com.abeerapps.kmovieapp.databinding.FragmentMovieDetailsBinding
import com.abeerapps.kmovieapp.domain.model.MovieDetails
import com.abeerapps.kmovieapp.ui.auth.SignInViewModel
import com.abeerapps.kmovieapp.ui.auth.showSignInPrompt
import com.abeerapps.kmovieapp.ui.common.TmdbImageSize
import com.abeerapps.kmovieapp.ui.common.applyStatusBarMargin
import com.abeerapps.kmovieapp.ui.common.applySystemBarsPadding
import com.abeerapps.kmovieapp.ui.common.collectWithViewLifecycle
import com.abeerapps.kmovieapp.ui.common.loadTmdbImage
import com.abeerapps.kmovieapp.ui.common.messageRes
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MovieDetailsFragment : Fragment(R.layout.fragment_movie_details) {

    private val viewModel: MovieDetailsViewModel by viewModels()
    private val signInViewModel: SignInViewModel by activityViewModels()

    private var _binding: FragmentMovieDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMovieDetailsBinding.bind(view)
        binding.backButton.applyStatusBarMargin()
        binding.favoriteButton.applyStatusBarMargin()
        binding.scrollContent.applySystemBarsPadding(bottom = true)

        val castAdapter = CastAdapter()
        binding.castList.adapter = castAdapter

        binding.backButton.setOnClickListener { findNavController().navigateUp() }
        binding.errorView.retryButton.setOnClickListener { viewModel.retry() }
        binding.favoriteButton.setOnClickListener { viewModel.onFavoriteClick() }

        collectWithViewLifecycle(viewModel.favoriteState, ::bindFavorite)
        collectWithViewLifecycle(viewModel.events) { event ->
            when (event) {
                MovieDetailsEvent.RequireSignIn -> showSignInPrompt(signInViewModel::startSignIn)
                is MovieDetailsEvent.ShowError ->
                    Snackbar.make(binding.root, event.error.messageRes(), Snackbar.LENGTH_SHORT).show()
            }
        }

        collectWithViewLifecycle(viewModel.uiState) { state ->
            val content = state as? MovieDetailsUiState.Content

            binding.shimmer.isVisible = state is MovieDetailsUiState.Loading
            if (state is MovieDetailsUiState.Loading) binding.shimmer.startShimmer() else binding.shimmer.stopShimmer()
            binding.scrollContent.isVisible = content != null
            binding.playTrailerButton.isVisible = content?.details?.trailer != null
            binding.favoriteButton.isVisible = content != null
            binding.errorView.root.isVisible = state is MovieDetailsUiState.Error
            if (state is MovieDetailsUiState.Error) binding.errorView.errorMessage.setText(state.error.messageRes())

            content?.details?.let { bindDetails(it, castAdapter) }
        }
    }

    private fun bindDetails(details: MovieDetails, castAdapter: CastAdapter) = with(binding) {
        poster.loadTmdbImage(details.posterPath ?: details.backdropPath, TmdbImageSize.POSTER_LARGE)
        poster.contentDescription = getString(R.string.cd_movie_poster, details.title)
        title.text = details.title
        tagline.text = details.tagline
        tagline.isVisible = details.tagline != null
        genres.text = details.genres.joinToString(GENRE_SEPARATOR) { it.name }
        genres.isVisible = details.genres.isNotEmpty()
        rating.text = getString(R.string.rating_format, details.rating)
        meta.text = buildMetaLine(details)
        overview.text = details.overview.ifBlank { getString(R.string.no_overview) }

        castAdapter.submitList(details.cast)
        castTitle.isVisible = details.cast.isNotEmpty()
        castList.isVisible = details.cast.isNotEmpty()

        val trailer = details.trailer
        playTrailerButton.setOnClickListener { trailer?.let { openUrl(it.url) } }
    }

    private fun bindFavorite(state: FavoriteUiState) = with(binding) {
        favoriteIcon.setImageResource(
            if (state.isFavorite) R.drawable.ic_favorite else R.drawable.ic_favorite_border,
        )
        favoriteButton.contentDescription = getString(
            if (state.isFavorite) R.string.cd_remove_favorite else R.string.cd_add_favorite,
        )
        favoriteButton.isEnabled = !state.isUpdating
    }

    private fun buildMetaLine(details: MovieDetails): String = listOfNotNull(
        details.releaseDate?.take(4),
        details.runtimeMinutes?.let { getString(R.string.runtime_format, it / 60, it % 60) },
        resources.getQuantityString(R.plurals.vote_count, details.voteCount, details.voteCount),
    ).joinToString(GENRE_SEPARATOR)

    private fun openUrl(url: String) {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, url.toUri()))
        } catch (_: ActivityNotFoundException) {
            Snackbar.make(binding.root, R.string.error_no_app_to_open_link, Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        binding.castList.adapter = null
        _binding = null
        super.onDestroyView()
    }

    private companion object {
        const val GENRE_SEPARATOR = " • "
    }
}
