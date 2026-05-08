package com.example.formfit.network

import com.example.formfit.datastore.SessionManager
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val sessionManager: SessionManager
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runBlocking {
            sessionManager.getAccessToken()
        }

        // Takes the original request and adds the Authorization header to it
        val request = chain.request().newBuilder()
            .apply {
                // token?.let means it only adds the header if the token is not null — so unauthenticated
                // requests like login and signup still work fine without a token
                token?.let {
                    addHeader("Authorization", "Bearer $it")
                }
            }
            .build()

        // Passes the modified request forward down the OkHttp chain to be sent to the backend
        return chain.proceed(request)
    }
}