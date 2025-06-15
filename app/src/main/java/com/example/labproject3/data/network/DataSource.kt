package com.example.labproject3.data.network

import android.content.Context
import com.example.labproject3.BuildConfig
import com.example.labproject3.data.TokenPreferences
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DataSource {
    private lateinit var tokenPreferences: TokenPreferences
    companion object {
        private const val BASE_URL = "https://react-midterm.kreosoft.space/api/"
    }

    fun <Api> buildAPI(
        context: Context,
        api: Class<Api>
    ) : Api {
        tokenPreferences = TokenPreferences(context)
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor { chain ->
                val originalResponse = chain.proceed(chain.request())
                if (originalResponse.code == 401) {
                    tokenPreferences.removeToken()
                }
                originalResponse
            }
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(api)
    }
}
