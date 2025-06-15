package com.example.labproject3.ui.home

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.labproject3.R
import com.example.labproject3.data.PassedData
import com.example.labproject3.data.response.MoviesItem
import com.example.labproject3.databinding.RecyclerFavouriteBinding
import com.example.labproject3.ui.details.MovieDetailsActivity

class FavouriteAdapter (
    private var favList: List<MoviesItem>,
    private var token: String,
    private var userID: String,
    private val context: Context,
    private val listener: RemoveFavListener
    ) : RecyclerView.Adapter<FavouriteAdapter.FavouriteViewHolder>() {

        class FavouriteViewHolder (binding: RecyclerFavouriteBinding) :
            RecyclerView.ViewHolder(binding.root) {
                val poster = binding.posterItem
                val remove = binding.removeFavorite
            }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteViewHolder {
        val binding = RecyclerFavouriteBinding.inflate(LayoutInflater.from(parent.context), null, false)
        return FavouriteViewHolder(binding)
    }

    override fun getItemCount(): Int = favList.size

    override fun onBindViewHolder(holder: FavouriteViewHolder, position: Int) {
        Glide.with(holder.itemView)
            .load(favList[position].poster)
            .into(holder.poster)

        holder.remove.setOnClickListener {
            val builder = AlertDialog.Builder(context, R.style.CustomAlertDialogTheme)
            builder.setMessage("Are you sure you want to remove this movie from your favorite?")
                .setCancelable(false)
                .setPositiveButton("Yes") { _, _ ->
                    listener.removeFavMovie(favList[position].id)
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
            val alert = builder.create()
            alert.show()
        }

        holder.itemView.setOnClickListener {
            val nextPage = Intent(holder.itemView.context, MovieDetailsActivity::class.java)
            nextPage.putExtra(PassedData.MOVIE_ID, favList[position].id)
            nextPage.putExtra(PassedData.TOKEN, token)
            nextPage.putExtra(PassedData.USER_ID, userID)
            nextPage.putExtra(PassedData.IS_FAV, "true")
            holder.itemView.context.startActivity(nextPage)
        }
    }

    interface RemoveFavListener{
        fun removeFavMovie(id: String)
    }
}