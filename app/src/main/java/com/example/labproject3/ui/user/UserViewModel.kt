package com.example.labproject3.ui.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.labproject3.data.network.Resource
import com.example.labproject3.data.repository.UserRepo
import com.example.labproject3.data.request.LoginRequest
import com.example.labproject3.data.request.RegisterRequest
import com.example.labproject3.data.request.UserUpdateRequest
import com.example.labproject3.data.response.LoginResponse
import com.example.labproject3.data.response.MessageTokenResponse
import com.example.labproject3.data.response.UserResponse
import com.example.labproject3.data.response.UserUpdateResponse
import kotlinx.coroutines.launch

class UserViewModel(
    private val repo: UserRepo
) : ViewModel(){

    private val _loginResponse : MutableLiveData<Resource<LoginResponse>> = MutableLiveData()
    val loginResponse: LiveData<Resource<LoginResponse>>
        get() = _loginResponse

    private val _logoutResponse : MutableLiveData<Resource<MessageTokenResponse>> = MutableLiveData()
    val logoutResponse: LiveData<Resource<MessageTokenResponse>>
        get() = _logoutResponse

    private val _getUserResponse : MutableLiveData<Resource<UserResponse>> = MutableLiveData()
    val getUserResponse: LiveData<Resource<UserResponse>>
        get() = _getUserResponse

    private val _upUserResponse : MutableLiveData<Resource<UserUpdateResponse>> = MutableLiveData()
    val upUserResponse: LiveData<Resource<UserUpdateResponse>>
        get() = _upUserResponse

    fun login (
        user: LoginRequest
    ) = viewModelScope.launch {
        _loginResponse.value = repo.login(user)
    }

    fun register (
        user: RegisterRequest
    ) = viewModelScope.launch {
        _loginResponse.value = repo.register(user)
    }

    fun logout (
    ) = viewModelScope.launch {
        _logoutResponse.value = repo.logout()
    }

    fun getUser (
        token: String
    ) = viewModelScope.launch {
        _getUserResponse.value = repo.getUser(token)
    }

    fun updateUser (
        token: String,
        user: UserUpdateRequest
    ) = viewModelScope.launch {
        _upUserResponse.value = repo.updateUser(token, user)
    }
}