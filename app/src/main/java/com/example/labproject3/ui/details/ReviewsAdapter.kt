package com.example.labproject3.ui.details

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.labproject3.R
import com.example.labproject3.data.response.ReviewsDetailsItem
import com.example.labproject3.databinding.RecyclerReviewBinding
import com.example.labproject3.ui.visible
import java.text.SimpleDateFormat
import java.util.*

class ReviewsAdapter (
    private val revList: List<ReviewsDetailsItem>,
    private var userID: String,
    private val context: Context,
    private val listener: ButtonListener
): RecyclerView.Adapter<ReviewsAdapter.ReviewsViewHolder>() {

    class ReviewsViewHolder (binding: RecyclerReviewBinding) :
        RecyclerView.ViewHolder(binding.root) {
            val profile = binding.profilePic
            val name = binding.nameReview
            val myReview = binding.myReview
            val rating = binding.movieRating
            val review = binding.contentReview
            val date = binding.dateReview
            val edit = binding.editReview
            val delete = binding.deleteReview
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewsViewHolder {
        val binding = RecyclerReviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReviewsViewHolder(binding)
    }

    override fun getItemCount(): Int = revList.size

    override fun onBindViewHolder(holder: ReviewsViewHolder, position: Int) {
        if (revList[position].isAnonymous) {
            Glide.with(holder.itemView)
                .load(R.drawable.ic_profile)
                .into(holder.profile)
            holder.profile.setImageResource(R.drawable.ic_profile)
            holder.name.text = "Анонимный пользователь"
            holder.myReview.visible(false)
        } else {
            Glide.with(holder.itemView)
                .load(revList[position].author.avatar)
                .error(R.drawable.ic_profile)
                .into(holder.profile)
            holder.name.text = revList[position].author.nickName
        }
        if (userID != revList[position].author.userId) {
            holder.edit.visible(false)
            holder.delete.visible(false)
        }
        val ratingInt = revList[position].rating
        holder.rating.text = ratingInt.toString()
        holder.rating.background.setTint(interpolateColors(ratingInt, 0, 10, R.color.accent, R.color.green_rating))
        holder.review.text = revList[position].reviewText
        val formattedDate = formatDate(revList[position].createDateTime)
        holder.date.text = formattedDate
        holder.edit.setOnClickListener {
            listener.updateReview(
                revList[position].id,
                revList[position].reviewText,
                revList[position].rating,
                revList[position].isAnonymous
            )
        }
        holder.delete.setOnClickListener {
            val builder = AlertDialog.Builder(context, R.style.CustomAlertDialogTheme)
            builder.setMessage("Are you sure you want to remove this review?")
                .setCancelable(false)
                .setPositiveButton("Yes") { _, _ ->
                    listener.deleteReview(revList[position].id)
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
            val alert = builder.create()
            alert.show()
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

    private fun formatDate(inputDate: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

        val date: Date = inputFormat.parse(inputDate) ?: return ""
        return outputFormat.format(date)
    }

    interface ButtonListener{
        fun updateReview(id: String, reviewText: String, rating: Int, isAnonymous: Boolean)
        fun deleteReview(id: String)

    }
}