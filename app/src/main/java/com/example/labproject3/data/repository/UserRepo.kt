package com.example.labproject3.data.repository

import com.example.labproject3.data.network.APIRequest
import com.example.labproject3.data.request.LoginRequest
import com.example.labproject3.data.request.RegisterRequest
import com.example.labproject3.data.request.UserUpdateRequest

class UserRepo(
    private val api: APIRequest
) : BaseRepo(){
    suspend fun login(
        user: LoginRequest
    ) = safeApiCall {
        api.login(user)
    }

    suspend fun register(
        user: RegisterRequest
    ) = safeApiCall {
        api.register(user)
    }

    suspend fun logout(
    ) = safeApiCall {
        api.logout()
    }

    suspend fun getUser(
        token: String
    ) = safeApiCall {
        api.getUser(token)
    }

    suspend fun updateUser(
        token: String,
        user: UserUpdateRequest
    ) = safeApiCall {
        api.updateUser(token, user)
    }
}