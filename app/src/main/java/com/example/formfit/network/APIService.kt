package com.example.formfit.network

import com.example.formfit.models.ChatRequest
import com.example.formfit.models.ChatResponse
import com.example.formfit.models.UserIssue
import com.example.formfit.models.LoginResponse
import com.example.formfit.models.ProfilePictureResponse
import com.example.formfit.models.RefreshRequest
import com.example.formfit.models.SignUpRequest
import com.example.formfit.models.TokenResponse
import com.example.formfit.models.UpdateUserRequest
import com.example.formfit.models.UpdateUserResponse
import com.example.formfit.models.UserResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part

interface ApiService {
    // -------------------------- POST --------------------------
    @FormUrlEncoded
    @POST("auth/token")
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password: String
    ): Response<LoginResponse>

    @POST("users")
    suspend fun signup(
        @Body request: SignUpRequest
    ) : Response<Unit>

    @POST("chat/")
    suspend fun sendMessage(
        @Body request: ChatRequest
    ) : Response<ChatResponse>

    @POST("refresh/")
    suspend fun refreshToken(
        @Body request: RefreshRequest
    ) : Response<TokenResponse>

    @Multipart
    @POST("cloudinary/upload-profile-picture")
    suspend fun uploadProfilePicture(
        @Part file: MultipartBody.Part
    ): Response<ProfilePictureResponse>

    // -------------------------- GET --------------------------
    @GET("users/me")
    suspend fun getCurrentUser(
        @Header("Authorization") token: String
    ): Response<UserResponse>

    @GET("issues/")
    suspend fun getUserIssues() : Response<List<UserIssue>>

    // -------------------------- PUT --------------------------
    @PUT("users/update-profile")
    suspend fun updateProfile(
        @Body request: UpdateUserRequest
    ) : Response<UpdateUserResponse>
}