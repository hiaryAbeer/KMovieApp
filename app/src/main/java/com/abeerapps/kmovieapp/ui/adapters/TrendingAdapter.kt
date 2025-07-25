package com.abeerapps.kmovieapp.ui.adapters

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.abeerapps.kmovieapp.databinding.RawTrendingBinding
import com.abeerapps.kmovieapp.domain.models.MovieModel
import com.abeerapps.kmovieapp.ui.MovieViewModel
import kotlinx.coroutines.InternalCoroutinesApi

@InternalCoroutinesApi
class TrendingAdapter(val list: List<MovieModel?>, val viewModel: MovieViewModel) :
    RecyclerView.Adapter<TrendingAdapter.TrendingViewHolder>() {

    private val imageURL = "https://image.tmdb.org/t/p/w500"

    inner class TrendingViewHolder(private val binding: RawTrendingBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) {
            try {
                binding.model = list[position]
                binding.vm = viewModel
                val uri = Uri.parse(imageURL + list[position]!!.posterPath)
                binding.rawTrendingImage.load(uri)
            } catch (e: Exception) {
                e.message?.let { Log.d("TrendingAdapter", it) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrendingViewHolder {
        val binding = RawTrendingBinding.inflate(LayoutInflater.from(parent.context))
        return TrendingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TrendingViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = list.size
}