package com.example.labproject3.data.network

sealed class Resource <out T> {
    data class Success<out T>(val value: T) : Resource<T>()
    data class Failure(
        val isNetworkError: Boolean,
        val errorThrowable: String?,
        val errorMessage: String?
    ) : Resource<Nothing>()

    object Loading : Resource<Nothing>()
}