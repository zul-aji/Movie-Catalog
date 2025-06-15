package com.example.labproject3.data.network

import com.example.labproject3.data.request.LoginRequest
import com.example.labproject3.data.request.RegisterRequest
import com.example.labproject3.data.request.ReviewRequest
import com.example.labproject3.data.request.UserUpdateRequest
import com.example.labproject3.data.response.*
import retrofit2.http.*

interface APIRequest {
    @POST("account/login")
    suspend fun login(
        @Body user: LoginRequest
    ) : LoginResponse

    @POST("account/register")
    suspend fun register(
        @Body user: RegisterRequest
    ) : LoginResponse

    @POST("account/logout")
    suspend fun logout(
    ) : MessageTokenResponse

    @GET("account/profile")
    suspend fun getUser(
        @Header ("Authorization") token : String
    ) : UserResponse

    @PUT("account/profile")
    suspend fun updateUser(
        @Header ("Authorization") token : String,
        @Body user : UserUpdateRequest
    ) : UserUpdateResponse

    @GET("favorites")
    suspend fun getFavorite(
        @Header ("Authorization") token : String
    ) : MovieResponse

    @POST("favorites/{id}/add")
    suspend fun addFavorite(
        @Header ("Authorization") token : String,
        @Path ("id") id : String
    ) : MessageTokenResponse

    @DELETE("favorites/{id}/delete")
    suspend fun removeFavorite(
        @Header ("Authorization") token : String,
        @Path ("id") id : String
    ) : MessageTokenResponse

    @GET("movies/{page}")
    suspend fun getGallery(
        @Path ("page") page : Int
    ) : MovieResponse

    @GET("movies/details/{id}")
    suspend fun getMovieDetails(
        @Path ("id") id : String
    ) : MovieDetailsResponse

    @POST("movie/{movieId}/review/add")
    suspend fun addReview(
        @Header ("Authorization") token : String,
        @Path("movieId") movieId: String,
        @Body review: ReviewRequest
    ) : MessageTokenResponse

    @PUT("movie/{movieId}/review/{id}/edit")
    suspend fun updateReview(
        @Header ("Authorization") token : String,
        @Path ("movieId") movieId : String,
        @Path ("id") id : String,
        @Body review: ReviewRequest
    ) : MessageTokenResponse

    @DELETE("movie/{movieId}/review/{id}/delete")
    suspend fun deleteReview(
        @Header ("Authorization") token : String,
        @Path ("movieId") movieId : String,
        @Path ("id") id : String
    ) : MessageTokenResponse
}