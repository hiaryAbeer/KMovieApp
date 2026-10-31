package com.abeerapps.kmovieapp.ui.home

import android.annotation.SuppressLint
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.abeerapps.kmovieapp.domain.model.Genre
import com.abeerapps.kmovieapp.ui.genre.GenreMoviesFragment

/** One page per genre. Item ids are genre ids, so pages survive list updates. */
class GenrePagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    private var genres: List<Genre> = emptyList()

    @SuppressLint("NotifyDataSetChanged")
    fun submitGenres(newGenres: List<Genre>) {
        if (newGenres == genres) return
        genres = newGenres
        notifyDataSetChanged()
    }

    fun genreAt(position: Int): Genre = genres[position]

    override fun getItemCount(): Int = genres.size

    override fun getItemId(position: Int): Long = genres[position].id.toLong()

    override fun containsItem(itemId: Long): Boolean = genres.any { it.id.toLong() == itemId }

    override fun createFragment(position: Int): Fragment =
        GenreMoviesFragment.newInstance(genres[position].id)
}
