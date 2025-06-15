package com.example.labproject3.data.response

import com.google.gson.annotations.SerializedName

data class MovieResponse(

    @field:SerializedName("movies")
	val movies: List<MoviesItem>,
    val pageInfo: PageInfo
)

data class GenresItem(

	@field:SerializedName("id")
	val id: String,

	@field:SerializedName("name")
	val name: String

)

data class ReviewsItem(

	@field:SerializedName("id")
	val id: String,

	@field:SerializedName("rating")
	val rating: Int

)

data class MoviesItem(

    @field:SerializedName("id")
	val id: String,

    @field:SerializedName("name")
	val name: String,

    @field:SerializedName("poster")
	val poster: String,

    @field:SerializedName("year")
	val year: Int,

    @field:SerializedName("country")
	val country: String,

    @field:SerializedName("genres")
	val genres: List<GenresItem>,

    @field:SerializedName("reviews")
	val reviews: List<ReviewsItem>

)

data class PageInfo(
	val pageSize: Int,
	val pageCount: Int,
	val currentPage: Int
)
