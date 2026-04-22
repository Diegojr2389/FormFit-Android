package com.example.formfit.network

import com.example.formfit.models.LoginRequest
import com.example.formfit.models.SignUpRequest
import com.example.formfit.models.TokenResponse
import com.example.formfit.models.UserResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded

interface ApiService {
    // -------------------------- POST --------------------------
    @FormUrlEncoded
    @POST("auth/token")
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password: String
    ): Response<TokenResponse>

    @POST("users")
    suspend fun signup(
        @Body request: SignUpRequest
    ) : Response<Unit>

    // -------------------------- GET --------------------------
    @GET("users/me")
    suspend fun getCurrentUser(
        @Header("Authorization") token: String
    ): Response<UserResponse>
}