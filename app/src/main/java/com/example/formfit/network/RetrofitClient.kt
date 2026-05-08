package com.example.formfit.network

import android.content.Context
import com.example.formfit.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
// Retrofit - A type-safe HTTP client library for Android that makes it easy to communicate with REST APIs
//  It is basically, a bridge between the frontend and the backend
// Retrofit Client - Creates and configures the retrofit instance
object RetrofitClient {
    // by lazy =  the retrofit instance will not be built until the first API call is made
    fun create(context: Context) : ApiService {
        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(OkHttpProvider.create(context))
            // GsonConverter - Automatically converts between Kotlin objects and JSON
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(ApiService::class.java)
    }
}