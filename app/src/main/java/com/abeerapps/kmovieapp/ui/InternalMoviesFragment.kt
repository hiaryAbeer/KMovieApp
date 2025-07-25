package com.abeerapps.kmovieapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.abeerapps.kmovieapp.R
import com.abeerapps.kmovieapp.databinding.InternalMoviesFragmentBinding
import com.abeerapps.kmovieapp.ui.adapters.MoviesByGenresAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch

@InternalCoroutinesApi
@AndroidEntryPoint
class InternalMoviesFragment : Fragment(R.layout.internal_movies_fragment) {

    private lateinit var mBinding: InternalMoviesFragmentBinding
    private val mViewModel: MovieViewModel by hiltNavGraphViewModels(R.id.main_nav_graph)
    private lateinit var mAdapter : MoviesByGenresAdapter

    companion object {
        private const val ARG_GENRES = "genres"
        
        fun newInstance(genres: Int): InternalMoviesFragment {
            val fragment = InternalMoviesFragment()
            val args = Bundle()
            args.putInt(ARG_GENRES, genres)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        mBinding = InternalMoviesFragmentBinding.inflate(layoutInflater)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val genres = arguments?.getInt(ARG_GENRES, 0) ?: 0
        mViewModel.getMovieByGenresData(genres)
        mBinding.rvMoviesByGenres.layoutManager = GridLayoutManager(requireContext(), 2)

        lifecycleScope.launch {
            mViewModel.mMoviesByGenresData.collect {
                mAdapter = MoviesByGenresAdapter(it.list, mViewModel)
                mBinding.rvMoviesByGenres.adapter = mAdapter
            }
        }
    }
}