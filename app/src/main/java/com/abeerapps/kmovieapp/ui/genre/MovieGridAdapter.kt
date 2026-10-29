package com.abeerapps.kmovieapp.ui.genre

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.abeerapps.kmovieapp.R
import com.abeerapps.kmovieapp.databinding.ItemMovieGridBinding
import com.abeerapps.kmovieapp.domain.model.Movie
import com.abeerapps.kmovieapp.ui.common.MovieDiffCallback
import com.abeerapps.kmovieapp.ui.common.TmdbImageSize
import com.abeerapps.kmovieapp.ui.common.loadTmdbImage

class MovieGridAdapter(
    private val onMovieClick: (Movie) -> Unit,
) : ListAdapter<Movie, MovieGridAdapter.ViewHolder>(MovieDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMovieGridBinding.inflate(
            LayoutInflater.from(parent.context), parent, false,
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(
        private val binding: ItemMovieGridBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                bindingAdapterPosition.takeIf { it != RecyclerView.NO_POSITION }
                    ?.let { onMovieClick(getItem(it)) }
            }
        }

        fun bind(movie: Movie) = with(binding) {
            val resources = root.resources
            title.text = movie.title
            poster.contentDescription = movie.title
            poster.loadTmdbImage(movie.posterPath, TmdbImageSize.POSTER)
            rating.text = resources.getString(R.string.rating_format, movie.rating)
            voteCount.text = resources.getQuantityString(
                R.plurals.vote_count, movie.voteCount, movie.voteCount,
            )
        }
    }
}
