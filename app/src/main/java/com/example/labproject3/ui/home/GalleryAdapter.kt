package com.example.labproject3.ui.home

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.labproject3.R
import com.example.labproject3.data.PassedData
import com.example.labproject3.data.response.MoviesItem
import com.example.labproject3.databinding.RecyclerGalleryBinding
import com.example.labproject3.ui.details.MovieDetailsActivity

class GalleryAdapter (
    private var galList: List<MoviesItem>,
    private var favIdList: List<MoviesItem>,
    private var token: String,
    private var userID: String,
    private val context: Context
    ) : RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder>() {

        class GalleryViewHolder (binding: RecyclerGalleryBinding) :
            RecyclerView.ViewHolder(binding.root) {
                val poster = binding.moviePoster
                val title = binding.movieTitle
                val yearCountry = binding.yearCountry
                val genre = binding.movieGenre
                val rating = binding.movieRating
            }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder {
        val binding = RecyclerGalleryBinding.inflate(LayoutInflater.from(parent.context), null, false)
        return GalleryViewHolder(binding)
    }

    override fun getItemCount(): Int = galList.size

    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
        Glide.with(holder.itemView)
            .load(galList[position].poster)
            .into(holder.poster)
        holder.title.text = galList[position].name
        holder.yearCountry.text = "${galList[position].year} • ${galList[position].country}"
        holder.genre.text = galList[position].genres.joinToString(separator = ", ") { it.name }
        val averageRating = galList[position].reviews.map { it.rating }.average()
        val oneDecRating = String.format("%.1f", averageRating)
        holder.rating.text = oneDecRating
        val color = interpolateColors(averageRating.toInt(), 0, 10, R.color.accent, R.color.green_rating)
        holder.rating.background.setTint(color)

        holder.itemView.setOnClickListener {
            val nextPage = Intent(holder.itemView.context, MovieDetailsActivity::class.java)
            nextPage.putExtra(PassedData.MOVIE_ID, galList[position].id)
            nextPage.putExtra(PassedData.TOKEN, token)
            nextPage.putExtra(PassedData.USER_ID, userID)

            val isFavorite = favIdList.any { movie -> movie.id == galList[position].id }
            if (isFavorite) {
                nextPage.putExtra(PassedData.IS_FAV, "true")
            } else {
                nextPage.putExtra(PassedData.IS_FAV, "false")
            }
            holder.itemView.context.startActivity(nextPage)
        }
    }

    private fun interpolateColors(value: Int, minValue: Int, maxValue: Int, startColor: Int, endColor: Int): Int {
        val ratio = (value - minValue).toFloat() / (maxValue - minValue)
        val startHsl = FloatArray(3)
        val endHsl = FloatArray(3)

        android.graphics.Color.colorToHSV(ContextCompat.getColor(context, startColor), startHsl)
        android.graphics.Color.colorToHSV(ContextCompat.getColor(context, endColor), endHsl)

        for (i in 0..2) {
            endHsl[i] = startHsl[i] + (endHsl[i] - startHsl[i]) * ratio
        }

        return android.graphics.Color.HSVToColor(endHsl)
    }
}