package com.abeerapps.kmovieapp.ui

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.size.Scale
import com.abeerapps.kmovieapp.databinding.RawMoviesByGenresBinding
import com.abeerapps.kmovieapp.domain.MovieModel
import kotlinx.coroutines.InternalCoroutinesApi

@InternalCoroutinesApi
class MoviesByGenresAdapter(val list: List<MovieModel>, val viewModel: MovieViewModel) :
    RecyclerView.Adapter<MoviesByGenresAdapter.TrendingViewHolder>() {

    private val imageURL = "https://image.tmdb.org/t/p/w500"

    inner class TrendingViewHolder(private val binding: RawMoviesByGenresBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(position: Int) {
            try {
                val uri = Uri.parse(imageURL + list[position].posterPath)
                binding.rawMoviesMovie.load(uri)
                binding.model = list[position]
                binding.vm = viewModel

            } catch (e: Exception) {
                e.message?.let { Log.d("MoviesByGenresAdapter", it) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrendingViewHolder {
        val binding = RawMoviesByGenresBinding.inflate(LayoutInflater.from(parent.context))
        return TrendingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TrendingViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = list.size
}