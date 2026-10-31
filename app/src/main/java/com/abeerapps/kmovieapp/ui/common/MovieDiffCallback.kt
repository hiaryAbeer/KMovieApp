package com.abeerapps.kmovieapp.ui.common

import androidx.recyclerview.widget.DiffUtil
import com.abeerapps.kmovieapp.domain.model.Movie

object MovieDiffCallback : DiffUtil.ItemCallback<Movie>() {
    override fun areItemsTheSame(oldItem: Movie, newItem: Movie) = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: Movie, newItem: Movie) = oldItem == newItem
}
