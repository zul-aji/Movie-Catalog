package com.example.labproject3.ui.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.labproject3.data.network.Resource
import com.example.labproject3.data.repository.MoviesRepo
import com.example.labproject3.data.request.ReviewRequest
import com.example.labproject3.data.response.*
import kotlinx.coroutines.launch

class MovieDetailsViewModel (
    private val repo: MoviesRepo
) : ViewModel(){
    private val _getMovieDetailsResponse : MutableLiveData<Resource<MovieDetailsResponse>> = MutableLiveData()
    val getMovieDetailsResponse: LiveData<Resource<MovieDetailsResponse>>
    get() = _getMovieDetailsResponse

    private val _addFavoriteResponse : MutableLiveData<Resource<MessageTokenResponse>> = MutableLiveData()
    val addFavoriteResponse: LiveData<Resource<MessageTokenResponse>>
        get() = _addFavoriteResponse

    private val _removeFavoriteResponse : MutableLiveData<Resource<MessageTokenResponse>> = MutableLiveData()
    val removeFavoriteResponse: LiveData<Resource<MessageTokenResponse>>
        get() = _removeFavoriteResponse

    private val _addReviewResponse : MutableLiveData<Resource<MessageTokenResponse>> = MutableLiveData()
    val addReviewResponse: LiveData<Resource<MessageTokenResponse>>
        get() = _addReviewResponse

    private val _updateReviewResponse : MutableLiveData<Resource<MessageTokenResponse>> = MutableLiveData()
    val updateReviewResponse: LiveData<Resource<MessageTokenResponse>>
        get() = _updateReviewResponse

    private val _deleteReviewResponse : MutableLiveData<Resource<MessageTokenResponse>> = MutableLiveData()
    val deleteReviewResponse: LiveData<Resource<MessageTokenResponse>>
        get() = _deleteReviewResponse

    fun getMovieDetails (
        id: String
    ) = viewModelScope.launch {
        _getMovieDetailsResponse.value = repo.getMovieDetails(id)
    }

    fun addFavorite (
        token: String,
        id: String
    ) = viewModelScope.launch {
        _addFavoriteResponse.value = repo.addFavorite(token, id)
    }

    fun removeFavorite (
        token: String,
        id: String
    ) = viewModelScope.launch {
        _removeFavoriteResponse.value = repo.removeFavorite(token, id)
    }

    fun addReview (
        token: String,
        movieId: String,
        review: ReviewRequest
    ) = viewModelScope.launch {
        _addReviewResponse.value = repo.addReview(token, movieId, review)
    }

    fun updateReview (
        token: String,
        movieId: String,
        id: String,
        review: ReviewRequest
    ) = viewModelScope.launch {
        _updateReviewResponse.value = repo.updateReview(token, movieId, id, review)
    }

    fun deleteReview (
        token: String,
        movieId: String,
        id: String,
    ) = viewModelScope.launch {
        _deleteReviewResponse.value = repo.deleteReview(token, movieId, id)
    }
}