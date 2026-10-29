package com.abeerapps.kmovieapp.ui.home

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.abeerapps.kmovieapp.databinding.FragmentHomeBinding
import com.abeerapps.kmovieapp.ui.DrawerHost
import com.abeerapps.kmovieapp.ui.common.BaseFragment
import com.abeerapps.kmovieapp.ui.common.applySystemBarsPadding
import com.abeerapps.kmovieapp.ui.common.collectWithViewLifecycle
import com.abeerapps.kmovieapp.ui.common.navigateSafely
import com.abeerapps.kmovieapp.ui.common.navigateToMovieDetails
import com.abeerapps.kmovieapp.ui.common.render
import com.abeerapps.kmovieapp.ui.common.setOnRetryClickListener
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    private val viewModel: HomeViewModel by viewModels()

    private val trendingAdapter = TrendingAdapter { navigateToMovieDetails(it.id) }
    private var genrePagerAdapter: GenrePagerAdapter? = null
    private var tabLayoutMediator: TabLayoutMediator? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.root.applySystemBarsPadding(top = true)

        setUpTrendingList()
        setUpGenreTabs()
        setUpClickListeners()
        collectWithViewLifecycle(viewModel.uiState, ::render)
    }

    private fun setUpTrendingList() {
        binding.trendingList.adapter = trendingAdapter
    }

    /** Tied to the view lifecycle so the pager doesn't leak while Home is on the back stack. */
    private fun setUpGenreTabs() {
        val pagerAdapter = GenrePagerAdapter(childFragmentManager, viewLifecycleOwner.lifecycle)
        binding.genrePager.adapter = pagerAdapter
        genrePagerAdapter = pagerAdapter
        tabLayoutMediator =
            TabLayoutMediator(binding.genreTabs, binding.genrePager) { tab, position ->
                tab.text = pagerAdapter.genreAt(position).name
            }.also { it.attach() }
    }

    private fun setUpClickListeners() = with(binding) {
        menuButton.setOnClickListener { (activity as? DrawerHost)?.openDrawer() }
        searchButton.setOnClickListener {
            navigateSafely(HomeFragmentDirections.actionHomeToSearch())
        }
        errorView.setOnRetryClickListener(viewModel::retry)
    }

    private fun render(state: HomeUiState) = with(binding) {
        trendingAdapter.submitList(state.trending)
        genrePagerAdapter?.submitGenres(state.genres)

        // Without genres there are no tabs, so loading and errors take over the whole screen.
        val hasContent = state.genres.isNotEmpty()
        contentGroup.isVisible = hasContent
        trendingGroup.isVisible = hasContent && state.trending.isNotEmpty()
        progressIndicator.isVisible = state.isLoading && !hasContent
        errorView.render(state.error.takeUnless { hasContent })
    }

    override fun onDestroyView() {
        tabLayoutMediator?.detach()
        tabLayoutMediator = null
        genrePagerAdapter = null
        binding.genrePager.adapter = null
        binding.trendingList.adapter = null
        super.onDestroyView()
    }
}
