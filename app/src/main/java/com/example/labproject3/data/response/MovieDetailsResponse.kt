package com.example.labproject3.data.response

import com.google.gson.annotations.SerializedName

data class MovieDetailsResponse(

    @field:SerializedName("country")
	val country: String,

    @field:SerializedName("fees")
	val fees: Int,

    @field:SerializedName("year")
	val year: Int,

    @field:SerializedName("director")
	val director: String,

    @field:SerializedName("description")
	val description: String,

    @field:SerializedName("ageLimit")
	val ageLimit: Int,

    @field:SerializedName("reviews")
	val reviews: List<ReviewsDetailsItem>,

    @field:SerializedName("genres")
	val genres: List<GenresDetailsItem>,

    @field:SerializedName("name")
	val name: String,

    @field:SerializedName("tagline")
	val tagline: String,

    @field:SerializedName("id")
	val id: String,

    @field:SerializedName("time")
	val time: Int,

    @field:SerializedName("poster")
	val poster: String,

    @field:SerializedName("budget")
	val budget: Int
)

data class Author(

	@field:SerializedName("nickName")
	val nickName: String,

	@field:SerializedName("avatar")
	val avatar: String,

	@field:SerializedName("userId")
	val userId: String
)

data class ReviewsDetailsItem(

    @field:SerializedName("isAnonymous")
	val isAnonymous: Boolean,

    @field:SerializedName("author")
	val author: Author,

    @field:SerializedName("rating")
	val rating: Int,

    @field:SerializedName("id")
	val id: String,

    @field:SerializedName("reviewText")
	val reviewText: String,

    @field:SerializedName("createDateTime")
	val createDateTime: String
)

data class GenresDetailsItem(

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("id")
	val id: String
)
