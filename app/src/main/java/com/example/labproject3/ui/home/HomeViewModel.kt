package com.example.labproject3.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.labproject3.data.network.Resource
import com.example.labproject3.data.repository.MoviesRepo
import com.example.labproject3.data.response.MessageTokenResponse
import com.example.labproject3.data.response.MovieResponse
import com.example.labproject3.data.response.UserResponse
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repo: MoviesRepo
) : ViewModel(){
    private val _getUserResponse : MutableLiveData<Resource<UserResponse>> = MutableLiveData()
    val getUserResponse: LiveData<Resource<UserResponse>>
        get() = _getUserResponse

    private val _getFavoriteResponse : MutableLiveData<Resource<MovieResponse>> = MutableLiveData()
    val getFavoriteResponse: LiveData<Resource<MovieResponse>>
        get() = _getFavoriteResponse

    private val _removeFavoriteResponse : MutableLiveData<Resource<MessageTokenResponse>> = MutableLiveData()
    val removeFavoriteResponse: LiveData<Resource<MessageTokenResponse>>
        get() = _removeFavoriteResponse

    private val _getGalleryResponse : MutableLiveData<Resource<MovieResponse>> = MutableLiveData()
    val getGalleryResponse: LiveData<Resource<MovieResponse>>
        get() = _getGalleryResponse

    fun getUser (
        token: String
    ) = viewModelScope.launch {
        _getUserResponse.value = repo.getUser(token)
    }

    fun getFavorite (
        token: String
    ) = viewModelScope.launch {
        _getFavoriteResponse.value = repo.getFavorite(token)
    }

    fun removeFavorite (
        token: String,
        id: String
    ) = viewModelScope.launch {
        _removeFavoriteResponse.value = repo.removeFavorite(token, id)
    }

    fun getGallery (
        page: Int
    ) = viewModelScope.launch {
        _getGalleryResponse.value = repo.getGallery(page)
    }
}