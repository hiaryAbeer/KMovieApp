package com.abeerapps.kmovieapp.ui

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.abeerapps.kmovieapp.R
import com.abeerapps.kmovieapp.databinding.MovieDetailsFragmentBinding
import com.abeerapps.kmovieapp.ui.adapters.CastAdapter
import com.google.android.material.shape.CornerFamily
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch

@InternalCoroutinesApi
@AndroidEntryPoint
class MovieDetailsFragment : Fragment(R.layout.movie_details_fragment) {

    private lateinit var mBinding: MovieDetailsFragmentBinding
    private val mViewModel: MovieViewModel by hiltNavGraphViewModels(R.id.main_nav_graph)
    private lateinit var mAdapter: CastAdapter

    companion object {
        val imageURL = "https://image.tmdb.org/t/p/w500"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        mBinding = MovieDetailsFragmentBinding.inflate(layoutInflater)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding.sflMovieDetailsFragment.startShimmerAnimation()
        mBinding.cvMovieDetails.visibility = View.INVISIBLE

        mViewModel.getCastData()
        mViewModel.getMovieDetails()
        mBinding.vm = mViewModel

        getMovieCast()
        getMovieDetails()

        mBinding.ivMovieDetailsPlayMovie.setOnClickListener {

        }

        mBinding.ivMovieDetailsBack.setOnClickListener {
            findNavController().popBackStack()
        }

    }

    private fun getMovieCast() {
        mBinding.recyclerViewCast.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        lifecycleScope.launch {
            mViewModel.mCastData.collect {
                mAdapter = CastAdapter(it)
                mBinding.recyclerViewCast.adapter = mAdapter
            }
        }
    }

    private fun getMovieDetails() {
        lifecycleScope.launch {
            mViewModel.mMovieDetails.collect {
                mBinding.sflMovieDetailsFragment.stopShimmerAnimation()
                mBinding.cvMovieDetails.visibility = View.VISIBLE

                mBinding.model = it

                mViewModel.mIsMovieHasVideo.set(it.video)
                Log.d("mIsMovieHasVideo", "${mViewModel.mIsMovieHasVideo.get()} ****** ${it.video}")

                var movieType = ""
                it.genresModelList?.map {
                    movieType += it?.name + " / "
                }
                mBinding.type = movieType.dropLast(2)

                val uri = Uri.parse(imageURL + it.movieImage)
                mBinding.cvMovieDetails.shapeAppearanceModel =
                    mBinding.cvMovieDetails.shapeAppearanceModel.toBuilder()
                        .setBottomLeftCorner(CornerFamily.ROUNDED, 28f)
                        .setBottomRightCorner(CornerFamily.ROUNDED, 28f)
                        .build()
                mBinding.cvMovieDetails.load(uri)
                Log.d("uri", "$uri")

            }
        }
    }
}