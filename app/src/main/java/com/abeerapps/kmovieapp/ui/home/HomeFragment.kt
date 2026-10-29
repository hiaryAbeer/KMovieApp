package com.abeerapps.kmovieapp.ui.home

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.abeerapps.kmovieapp.R
import com.abeerapps.kmovieapp.databinding.FragmentHomeBinding
import com.abeerapps.kmovieapp.ui.common.applySystemBarsPadding
import com.abeerapps.kmovieapp.ui.common.collectWithViewLifecycle
import com.abeerapps.kmovieapp.ui.common.messageRes
import com.abeerapps.kmovieapp.ui.common.navigateSafely
import com.abeerapps.kmovieapp.ui.common.navigateToMovieDetails
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    private val viewModel: HomeViewModel by viewModels()

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private var tabLayoutMediator: TabLayoutMediator? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)
        binding.root.applySystemBarsPadding(top = true)

        val trendingAdapter = TrendingAdapter { navigateToMovieDetails(it.id) }
        binding.trendingList.adapter = trendingAdapter

        // Tied to the view lifecycle so the pager doesn't leak when Home is on the back stack.
        val pagerAdapter = GenrePagerAdapter(childFragmentManager, viewLifecycleOwner.lifecycle)
        binding.genrePager.adapter = pagerAdapter
        tabLayoutMediator = TabLayoutMediator(binding.genreTabs, binding.genrePager) { tab, position ->
            tab.text = pagerAdapter.genreAt(position).name
        }.also { it.attach() }

        binding.errorView.retryButton.setOnClickListener { viewModel.retry() }
        binding.searchButton.setOnClickListener {
            navigateSafely(HomeFragmentDirections.actionHomeToSearch())
        }
        binding.favoritesButton.setOnClickListener {
            navigateSafely(HomeFragmentDirections.actionHomeToFavorites())
        }

        collectWithViewLifecycle(viewModel.uiState) { state ->
            trendingAdapter.submitList(state.trending)
            pagerAdapter.submitGenres(state.genres)

            val hasContent = state.genres.isNotEmpty()
            binding.contentGroup.isVisible = hasContent
            binding.trendingGroup.isVisible = hasContent && state.trending.isNotEmpty()
            binding.progressIndicator.isVisible = state.isLoading && !hasContent
            binding.errorView.root.isVisible = state.error != null && !hasContent
            state.error?.let { binding.errorView.errorMessage.setText(it.messageRes()) }
        }
    }

    override fun onDestroyView() {
        tabLayoutMediator?.detach()
        tabLayoutMediator = null
        binding.genrePager.adapter = null
        binding.trendingList.adapter = null
        _binding = null
        super.onDestroyView()
    }
}
