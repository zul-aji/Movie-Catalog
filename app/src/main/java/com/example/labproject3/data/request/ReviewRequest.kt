package com.example.labproject3.data.request

data class ReviewRequest(
	val reviewText: String,
	val rating: Int,
	val isAnonymous: Boolean
)

