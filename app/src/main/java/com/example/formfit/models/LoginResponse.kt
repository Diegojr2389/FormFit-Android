package com.example.formfit.models

import com.google.gson.annotations.SerializedName

data class LoginResponse (
    @SerializedName("access_token")
    val accessToken: String,
    @SerializedName("refresh_token")
    val refreshToken: String,
    @SerializedName("token_type")
    val tokenType: String,
    @SerializedName("user_id")
    val userId: Int,
    val username: String,
    val email: String,
    @SerializedName("profile_picture_url")
    val profilePictureUrl: String?
)