package com.abeerapps.kmovieapp.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.abeerapps.kmovieapp.databinding.ItemTrendingMovieBinding
import com.abeerapps.kmovieapp.domain.model.Movie
import com.abeerapps.kmovieapp.ui.common.MovieDiffCallback
import com.abeerapps.kmovieapp.ui.common.TmdbImageSize
import com.abeerapps.kmovieapp.ui.common.loadTmdbImage

class TrendingAdapter(
    private val onMovieClick: (Movie) -> Unit,
) : ListAdapter<Movie, TrendingAdapter.ViewHolder>(MovieDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTrendingMovieBinding.inflate(
            LayoutInflater.from(parent.context), parent, false,
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(
        private val binding: ItemTrendingMovieBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                bindingAdapterPosition.takeIf { it != RecyclerView.NO_POSITION }
                    ?.let { onMovieClick(getItem(it)) }
            }
        }

        fun bind(movie: Movie) = with(binding) {
            title.text = movie.title
            backdrop.contentDescription = movie.title
            backdrop.loadTmdbImage(movie.backdropPath ?: movie.posterPath, TmdbImageSize.BACKDROP)
        }
    }
}
