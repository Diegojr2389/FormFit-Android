package com.example.formfit.network

import android.content.Context
import com.example.formfit.BuildConfig
import com.example.formfit.datastore.SessionManager
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// A singleton — 'object' in Kotlin means only one instance ever exists
// Only ever want one HTTP client in an app
object OkHttpProvider {

    fun create(context: Context): OkHttpClient {

        val sessionManager = SessionManager(context)

        // Separate retrofit for refresh calls
        val refreshRetrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(
                GsonConverterFactory.create()
            )
            .build()

        val refreshApiService =
            refreshRetrofit.create(ApiService::class.java)

        return OkHttpClient.Builder()
            .addInterceptor(
                AuthInterceptor(sessionManager) // attaches the access token to every outgoing request
            )
            .authenticator(
                TokenAuthenticator( //  handles 401s by refreshing the token and retrying
                    sessionManager,
                    refreshApiService
                )
            )
            .build()
    }
}