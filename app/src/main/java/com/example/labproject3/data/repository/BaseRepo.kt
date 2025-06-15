package com.example.labproject3.data.repository

import com.example.labproject3.data.network.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import retrofit2.HttpException

abstract class BaseRepo {
    suspend fun <T> safeApiCall(
        apiCall: suspend () -> T
    ) : Resource<T> {
        return withContext(Dispatchers.IO){
            try{
                Resource.Success(apiCall.invoke())
            } catch (throwable: Throwable) {
                when (throwable) {
                    is HttpException -> {
                        val errorResponse = throwable.response()?.errorBody()?.string()
                        val errorMessage = parseErrorMessage(errorResponse)
                        Resource.Failure(false, "${throwable.code()} ${throwable.message()}", errorMessage)
                    }
                    else -> {
                        Resource.Failure(true, null, throwable.message)
                    }
                }
            }
        }
    }
    private fun parseErrorMessage(errorResponse: String?): String? {
        return try {
            val jsonObject = errorResponse?.let { JSONObject(it) }
            jsonObject?.getString("message")
        } catch (e: JSONException) {
            null
        }
    }
}