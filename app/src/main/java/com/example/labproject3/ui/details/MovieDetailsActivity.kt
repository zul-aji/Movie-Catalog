package com.example.labproject3.ui.details

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.labproject3.R
import com.example.labproject3.data.PassedData
import com.example.labproject3.data.network.APIRequest
import com.example.labproject3.data.network.DataSource
import com.example.labproject3.data.network.Resource
import com.example.labproject3.data.repository.MoviesRepo
import com.example.labproject3.data.request.ReviewRequest
import com.example.labproject3.data.response.ReviewsDetailsItem
import com.example.labproject3.databinding.ActivityMovieDetailsBinding
import com.example.labproject3.ui.base.ViewModelFactory
import com.example.labproject3.ui.visible
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

class MovieDetailsActivity : AppCompatActivity(), ReviewsAdapter.ButtonListener {

    private lateinit var binding: ActivityMovieDetailsBinding
    private lateinit var movieDetailsView: MovieDetailsViewModel
    private lateinit var revAdapter: ReviewsAdapter
    private lateinit var token: String
    private lateinit var userID: String
    private lateinit var movieID: String
    private lateinit var isFav: String
    private lateinit var starRating: Array<ImageView>
    private var currentRating: Int = 0
    private val dataSource = DataSource()
    private var reviewState =  "mt"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMovieDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val factory = ViewModelFactory(MoviesRepo(dataSource.buildAPI(this, APIRequest::class.java)))
        movieDetailsView = ViewModelProvider(this, factory)[MovieDetailsViewModel::class.java]

        token = intent.getStringExtra(PassedData.TOKEN).toString()
        userID = intent.getStringExtra(PassedData.USER_ID).toString()
        isFav = intent.getStringExtra(PassedData.IS_FAV).toString()
        movieID = "mt"

        binding.arrowBack.setOnClickListener {
            finish()
        }


        intent.getStringExtra(PassedData.MOVIE_ID)?.let { movieDetailsView.getMovieDetails(it) }
        movieDetailsView.getMovieDetailsResponse.observe(this){
            when (it) {
                is Resource.Success -> {
                    binding.progressCircle.visible(false)
                    movieID = it.value.id
                    Glide.with(this)
                        .load(it.value.poster)
                        .into(binding.moviePoster)
                    binding.movieTitle.text = it.value.name
                    binding.movieSynopsis.text = it.value.description
                    binding.movieYear.text = it.value.year.toString()
                    binding.movieCountry.text = it.value.country
                    binding.movieDuration.text = it.value.time.toString()
                    binding.movieDirector.text = it.value.director
                    binding.movieBudget.text = "$${formatMoney(it.value.budget)}"
                    binding.movieFees.text = "$${formatMoney(it.value.fees)}"
                    binding.movieAgeLimit.text = "${it.value.ageLimit}+"

                    // the button at the top of the screen changes
                    // if the it is in favorite or not
                    if (isFav == "true") {
                        binding.addFav.text = "Remove Favourite"
                    } else {
                        binding.addFav.text = "Add Favourite"
                        binding.addFav.setTextColor(ContextCompat.getColor(this, R.color.bright_white))
                        binding.addFav.setBackgroundResource(R.drawable.fav_add)
                    }

                    // flexbox populating the genre with TextViews
                    val flexboxLayout = binding.flexGenre
                    if (flexboxLayout.childCount == 0) {
                        it.value.genres.forEachIndexed{_, genreItem ->
                            val textView = TextView(this)

                            val layoutParams = LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                            )
                            layoutParams.setMargins(0, 0, 8.dpToPx(), 8.dpToPx())
                            textView.layoutParams = layoutParams
                            textView.setPadding(16.dpToPx(), 6.dpToPx(), 16.dpToPx(), 6.dpToPx())
                            textView.setBackgroundResource(R.drawable.genre_bg)
                            textView.setTextColor(ContextCompat.getColor(this, R.color.bright_white))
                            textView.textSize = 12f
                            textView.typeface = ResourcesCompat.getFont(this, R.font.ibm_plex_sans)
                            textView.text = genreItem.name

                            flexboxLayout.addView(textView)
                        }
                    }

                    if (it.value.reviews.isEmpty()) {
                        binding.noReview.visible(true)
                    } else {
                        binding.noReview.visible(false)
                        val revList = ArrayList<ReviewsDetailsItem>()
                        it.value.reviews.forEachIndexed { _, reviewsDetailsItem ->
                            val reviews = ReviewsDetailsItem(
                                isAnonymous = reviewsDetailsItem.isAnonymous,
                                author = reviewsDetailsItem.author,
                                rating = reviewsDetailsItem.rating,
                                id = reviewsDetailsItem.id,
                                reviewText = reviewsDetailsItem.reviewText,
                                createDateTime = reviewsDetailsItem.createDateTime
                            )
                            revList.add(reviews)
                            // to put the current user review on top
                            val customComparator = Comparator<ReviewsDetailsItem> { item, _ ->
                                when (item.author.userId) {
                                    userID -> -1
                                    else -> 0
                                }
                            }
                            revList.sortWith(customComparator)

                            revAdapter = ReviewsAdapter(revList, userID, this, this)
                            binding.reviewRecycler.adapter = revAdapter
                        }
                    }
                }
                is Resource.Failure -> {
                    binding.progressCircle.visible(false)
                    Toast.makeText(
                        this,
                        if (it.isNetworkError){ "Network Error" }
                        else { "Fail: ${it.errorMessage.toString()}" },
                        Toast.LENGTH_SHORT
                    ).show()
                }
                Resource.Loading -> {
                    binding.progressCircle.visible(true)
                }
                else -> {
                    binding.progressCircle.visible(false)
                    Toast.makeText(
                        this,
                        getString(R.string.unknown_err),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        binding.addFav.setOnClickListener {
            if (isFav == "true") {
                val builder = AlertDialog.Builder(this@MovieDetailsActivity, R.style.CustomAlertDialogTheme)
                builder.setMessage(getString(R.string.delete_fav))
                    .setCancelable(false)
                    .setPositiveButton("Yes") { _, _ ->
                        movieDetailsView.removeFavorite(token, intent.getStringExtra(PassedData.MOVIE_ID).toString())
                    }
                    .setNegativeButton("No") { dialog, _ ->
                        dialog.dismiss()
                    }
                val alert = builder.create()
                alert.show()
            } else {
                movieDetailsView.addFavorite(token, intent.getStringExtra(PassedData.MOVIE_ID).toString())
            }
        }

        movieDetailsView.removeFavoriteResponse.observe(this) {
            when (it) {
                is Resource.Failure -> {
                    binding.progressCircle.visible(false)
                    if (it.isNetworkError){
                        val refresh = Intent(this, MovieDetailsActivity::class.java)
                        refresh.putExtra(PassedData.MOVIE_ID, intent.getStringExtra(PassedData.MOVIE_ID).toString())
                        refresh.putExtra(PassedData.TOKEN, token)
                        refresh.putExtra(PassedData.USER_ID, userID)
                        refresh.putExtra(PassedData.IS_FAV, "false")
                        startActivity(refresh)
                        finish()
                        Toast.makeText(
                            this,"Removed from favourite",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            this, "Fail: ${it.errorMessage.toString()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                Resource.Loading -> {
                    binding.progressCircle.visible(true)
                }
                else -> {
                    binding.progressCircle.visible(false)
                    Toast.makeText(
                        this,
                        getString(R.string.unknown_err),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        movieDetailsView.addFavoriteResponse.observe(this) {
            when (it) {
                is Resource.Failure -> {
                    binding.progressCircle.visible(false)
                    if (it.isNetworkError){
                        val refresh = Intent(this, MovieDetailsActivity::class.java)
                        refresh.putExtra(PassedData.MOVIE_ID, intent.getStringExtra(PassedData.MOVIE_ID).toString())
                        refresh.putExtra(PassedData.TOKEN, token)
                        refresh.putExtra(PassedData.USER_ID, userID)
                        refresh.putExtra(PassedData.IS_FAV, "true")
                        startActivity(refresh)
                        finish()
                        Toast.makeText(
                            this,"Added to favourite",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            this, "Fail: ${it.errorMessage.toString()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                Resource.Loading -> {
                    binding.progressCircle.visible(true)
                }
                else -> {
                    binding.progressCircle.visible(false)
                    Toast.makeText(
                        this,
                        getString(R.string.unknown_err),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        val reviewDialog = Dialog(this)
        reviewDialog.setContentView(R.layout.review_dialog_layout)
        val closeButton = reviewDialog.findViewById<TextView>(R.id.cancel_button)
        closeButton.setOnClickListener {
            reviewDialog.dismiss()
        }

        reviewDialog.findViewById<CheckBox>(R.id.is_anon_cb).visible(true)
        reviewDialog.findViewById<CheckBox>(R.id.is_anon).visible(true)

        starRating = arrayOf(
            reviewDialog.findViewById(R.id.star_1),
            reviewDialog.findViewById(R.id.star_2),
            reviewDialog.findViewById(R.id.star_3),
            reviewDialog.findViewById(R.id.star_4),
            reviewDialog.findViewById(R.id.star_5),
            reviewDialog.findViewById(R.id.star_6),
            reviewDialog.findViewById(R.id.star_7),
            reviewDialog.findViewById(R.id.star_8),
            reviewDialog.findViewById(R.id.star_9),
            reviewDialog.findViewById(R.id.star_10)
        )

        for (i in starRating.indices) {
            starRating[i].setOnClickListener { onStarClicked(i + 1) }
        }

        reviewDialog.findViewById<TextView>(R.id.clear_star).setOnClickListener {
            onStarClicked(0)
        }

        reviewDialog.findViewById<Button>(R.id.save_review).setOnClickListener {
            val revText = reviewDialog.findViewById<TextView>(R.id.review_content).text.toString()
            val isAnon = reviewDialog.findViewById<CheckBox>(R.id.is_anon_cb).isChecked
            val review = ReviewRequest(revText, currentRating, isAnon)
            if (revText.isEmpty()) {
                val builder = AlertDialog.Builder(this@MovieDetailsActivity, R.style.CustomAlertDialogTheme)
                builder.setMessage(getString(R.string.mt_rev))
                    .setCancelable(false)
                    .setNegativeButton("Ok") { dialog, _ ->
                        dialog.dismiss()
                    }
                val alert = builder.create()
                alert.show()
            } else {
                movieDetailsView.addReview(token, movieID, review)
                reviewState = "add"
                reviewDialog.dismiss()
                recreate()
            }
        }

        movieDetailsView.addReviewResponse.observe(this) {
            when (it) {
                is Resource.Failure -> {
                    binding.progressCircle.visible(false)
                    if (reviewState == "add") {
                        Toast.makeText(
                            this,
                            if (it.isNetworkError) { "Review Added" }
                            else { "Fail: ${it.errorMessage.toString()}" },
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                Resource.Loading -> {
                    binding.progressCircle.visible(true)
                }
                else -> {
                    binding.progressCircle.visible(false)
                    Toast.makeText(
                        this,
                        getString(R.string.unknown_err),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        movieDetailsView.updateReviewResponse.observe(this) {
            when (it) {
                is Resource.Failure -> {
                    binding.progressCircle.visible(false)
                    if (reviewState == "edit") {
                        Toast.makeText(
                            this,
                            if (it.isNetworkError) { "Review Updated" }
                            else { "Fail: ${it.errorMessage.toString()}" },
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                Resource.Loading -> {
                    binding.progressCircle.visible(true)
                }
                else -> {
                    binding.progressCircle.visible(false)
                    Toast.makeText(
                        this,
                        getString(R.string.unknown_err),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        movieDetailsView.deleteReviewResponse.observe(this) {
            when (it) {
                is Resource.Failure -> {
                    binding.progressCircle.visible(false)
                    if (reviewState == "delete") {
                        Toast.makeText(
                            this,
                            if (it.isNetworkError) { "Review Deleted" }
                            else { "Fail: ${it.errorMessage.toString()}" },
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                Resource.Loading -> {
                    binding.progressCircle.visible(true)
                }
                else -> {
                    binding.progressCircle.visible(false)
                    Toast.makeText(
                        this,
                        getString(R.string.unknown_err),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        binding.addReview.setOnClickListener{
            reviewDialog.show()
        }
    }

    private fun formatMoney(value: Int): String {
        val symbols = DecimalFormatSymbols(Locale.getDefault())
        symbols.groupingSeparator = ' '
        val formatter = DecimalFormat("#,##0", symbols)
        return formatter.format(value)
    }

    private fun Int.dpToPx(): Int {
        return (this * Resources.getSystem().displayMetrics.density).toInt()
    }

    private fun onStarClicked(selectedRating: Int) {
        for (i in starRating.indices) {
            starRating[i].setImageResource(
                if (i < selectedRating) R.drawable.star_picked
                else R.drawable.star_unpicked
            )
        }
        currentRating = selectedRating
    }

    override fun updateReview(id: String, reviewText: String, rating: Int, isAnonymous: Boolean) {
        val reviewDialog = Dialog(this)
        Log.e("Anon Button", isAnonymous.toString() )
        reviewDialog.setContentView(R.layout.review_dialog_layout)
        reviewDialog.show()
        reviewDialog.findViewById<TextView>(R.id.cancel_button).setOnClickListener {
            reviewDialog.dismiss()
        }

        starRating = arrayOf(
            reviewDialog.findViewById(R.id.star_1),
            reviewDialog.findViewById(R.id.star_2),
            reviewDialog.findViewById(R.id.star_3),
            reviewDialog.findViewById(R.id.star_4),
            reviewDialog.findViewById(R.id.star_5),
            reviewDialog.findViewById(R.id.star_6),
            reviewDialog.findViewById(R.id.star_7),
            reviewDialog.findViewById(R.id.star_8),
            reviewDialog.findViewById(R.id.star_9),
            reviewDialog.findViewById(R.id.star_10)
        )

        reviewDialog.findViewById<TextView>(R.id.review_content).text = reviewText

        // if the user checked the anonymous checkbox when adding the review, the checkbox will show
        if (isAnonymous) {
            reviewDialog.findViewById<CheckBox>(R.id.is_anon_cb).visible(true)
            reviewDialog.findViewById<CheckBox>(R.id.is_anon).visible(true)
            reviewDialog.findViewById<CheckBox>(R.id.is_anon_cb).isChecked = isAnonymous
        // if the user does not checked the anonymous checkbox when adding the review,
        // the checkbox will not show
        } else {
            reviewDialog.findViewById<CheckBox>(R.id.is_anon_cb).visible(false)
            reviewDialog.findViewById<CheckBox>(R.id.is_anon).visible(false)
        }
        onStarClicked(rating)

        for (i in starRating.indices) {
            starRating[i].setOnClickListener { onStarClicked(i + 1) }
        }

        reviewDialog.findViewById<TextView>(R.id.clear_star).setOnClickListener {
            onStarClicked(0)
        }

        reviewDialog.findViewById<Button>(R.id.save_review).setOnClickListener {
            val revText = reviewDialog.findViewById<TextView>(R.id.review_content).text.toString()
            val isAnon = reviewDialog.findViewById<CheckBox>(R.id.is_anon_cb).isChecked
            val review = ReviewRequest(revText, currentRating, isAnon)
            movieDetailsView.updateReview(token, movieID, id, review)
            reviewState = "edit"
            reviewDialog.dismiss()
            recreate()
        }
    }

    override fun deleteReview(id: String) {
        movieDetailsView.deleteReview(token, movieID, id)
        reviewState = "delete"
        recreate()
    }
}