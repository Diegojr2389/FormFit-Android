package com.example.formfit.network

import com.example.formfit.datastore.SessionManager
import com.example.formfit.models.RefreshRequest
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class TokenAuthenticator(
    private val sessionManager: SessionManager,
    private val apiService: ApiService
) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        // runBlocking is used because OkHttp runs on a background thread and doesn't support coroutines
        // natively, so it blocks the thread until the value is retrieved
        val refreshToken = runBlocking {
            sessionManager.getRefreshToken()
        } ?: return null

        return try {

            // Hits FastAPI /refresh endpoint with the refresh token to get new tokens back
            val tokenResponse = runBlocking {
                apiService.refreshToken(RefreshRequest(refreshToken))
            }

            // save new tokens
            runBlocking {
                sessionManager.saveTokens(
                    tokenResponse.body()!!.accessToken,
                    tokenResponse.body()!!.refreshToken
                )
            }

            // Takes original failed request, attaches new access token, and returns it so that OkHttp
            // automatically retries it—done in the background
            response.request.newBuilder()
                .header(
                    "Authorization",
                    "Bearer ${tokenResponse.body()!!.accessToken}"
                )
                .build()
        // If the refresh fails (refresh token expired), clear all stored tokens and return null — OkHttp
        // passes the 401 back to the caller, then sends user to the login screen
        } catch (e: Exception) {
            runBlocking {
                sessionManager.clearSession()
            }

            return null
        }
    }
}