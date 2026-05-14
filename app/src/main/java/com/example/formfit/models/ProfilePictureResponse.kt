package com.example.formfit.models

import com.google.gson.annotations.SerializedName

data class ProfilePictureResponse(
    @SerializedName("profile_picture_url")
    val profilePictureUrl: String
)