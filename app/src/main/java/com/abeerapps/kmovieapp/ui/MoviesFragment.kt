package com.abeerapps.kmovieapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.abeerapps.kmovieapp.R
import com.abeerapps.kmovieapp.databinding.MoviesFragmentBinding
import com.abeerapps.kmovieapp.ui.adapters.TabsAdapter
import com.abeerapps.kmovieapp.ui.adapters.TrendingAdapter
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch

@InternalCoroutinesApi
@AndroidEntryPoint
class MoviesFragment : Fragment(R.layout.movies_fragment) {

    private lateinit var mBinding: MoviesFragmentBinding
    private val mViewModel: MovieViewModel by hiltNavGraphViewModels(R.id.main_nav_graph)
    private lateinit var mTrendingAdapter: TrendingAdapter
    private lateinit var mTabsAdapter: TabsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        mBinding = MoviesFragmentBinding.inflate(layoutInflater)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupTabsAdViewPager()

        setupTrendingList()

        lifecycleScope.launch {
            mViewModel.mAction.collect {
                if (it is MovieViewModel.MovieActions.NavToMovieDetails) {
                    val destination =
                        MoviesFragmentDirections.actionMoviesFragmentToMovieDetailsFragment()
                    findNavController().navigate(destination)
                }

            }

        }
    }

    private fun setupTrendingList() {
        mBinding.recyclerViewTrending.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        lifecycleScope.launch {
            mViewModel.mBaseData.collect {
                mTrendingAdapter = TrendingAdapter(it.list, viewModel = mViewModel)
                mBinding.recyclerViewTrending.adapter = mTrendingAdapter
            }
        }
    }

    private fun setupTabsAdViewPager() {
        lifecycleScope.launch {
            mViewModel.mGenresData.collect {
                mTabsAdapter = TabsAdapter(this@MoviesFragment, it.genresModelList)
                mBinding.moviesGenresVp.adapter = mTabsAdapter

                if (it.genresModelList.isNotEmpty())
                    TabLayoutMediator(mBinding.tabs, mBinding.moviesGenresVp) { tab, position ->
                        tab.text = it.genresModelList[position].name
                    }.attach()
            }
        }
    }
}