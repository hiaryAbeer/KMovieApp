package com.abeerapps.kmovieapp.ui

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.abeerapps.kmovieapp.databinding.RawCastBinding
import com.abeerapps.kmovieapp.domain.CastModel
import kotlinx.coroutines.InternalCoroutinesApi

@InternalCoroutinesApi
class CastAdapter(val list: List<CastModel>) :
    RecyclerView.Adapter<CastAdapter.TrendingViewHolder>() {

    private val imageURL = "https://image.tmdb.org/t/p/w500"

    inner class TrendingViewHolder(private val binding: RawCastBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(position: Int) {
            try {
                val uri = Uri.parse(imageURL + list[position].profile_path)
                binding.rawCastImage.load(uri)

            } catch (e: Exception) {
                e.message?.let { Log.d("CastAdapter", it) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrendingViewHolder {
        val binding = RawCastBinding.inflate(LayoutInflater.from(parent.context))
        return TrendingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TrendingViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = list.size
}