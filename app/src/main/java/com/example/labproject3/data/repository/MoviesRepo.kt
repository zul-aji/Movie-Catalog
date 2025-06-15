package com.example.labproject3.data.repository

import com.example.labproject3.data.network.APIRequest
import com.example.labproject3.data.request.ReviewRequest

class MoviesRepo(
    private val api: APIRequest
) : BaseRepo(){
    suspend fun getUser(
        token: String
    ) = safeApiCall {
        api.getUser(token)
    }

    suspend fun getGallery(
        page: Int
    ) = safeApiCall {
        api.getGallery(page)
    }

    suspend fun getFavorite(
        token: String
    ) = safeApiCall {
        api.getFavorite(token)
    }

    suspend fun addFavorite(
        token: String,
        id: String
    ) = safeApiCall {
        api.addFavorite(token, id)
    }

    suspend fun removeFavorite(
        token: String,
        id: String
    ) = safeApiCall {
        api.removeFavorite(token, id)
    }

    suspend fun getMovieDetails(
        id: String
    ) = safeApiCall {
        api.getMovieDetails(id)
    }

    suspend fun addReview(
        token: String,
        movieId: String,
        review: ReviewRequest
    ) = safeApiCall {
        api.addReview(token, movieId, review)
    }

    suspend fun updateReview(
        token: String,
        movieId: String,
        id: String,
        review: ReviewRequest
    ) = safeApiCall {
        api.updateReview(token, movieId, id, review)
    }

    suspend fun deleteReview(
        token: String,
        movieId: String,
        id: String
    ) = safeApiCall {
        api.deleteReview(token, movieId, id)
    }

}