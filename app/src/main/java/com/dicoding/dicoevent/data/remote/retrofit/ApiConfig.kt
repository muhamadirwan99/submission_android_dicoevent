package com.dicoding.dicoevent.data.remote.retrofit

import com.dicoding.dicoevent.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiConfig {
    companion object  {
        private const val BASE_URL = BuildConfig.BASE_URL

        fun getApiService(): ApiService {
            // Create logging interceptor to log HTTP request and response data
            val loggingInterceptor = if(BuildConfig.DEBUG) {
                HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
            } else {
                HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.NONE)
            }

            // Create OkHttpClient and add the logging interceptor
            val client = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build()

            // Create Retrofit instance with base URL and Gson converter
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()

            return retrofit.create(ApiService::class.java)
        }
    }
}