package com.example.formfit.models

import com.google.gson.annotations.SerializedName

data class UpdateUserRequest(
    val username: String,
    val email: String,
    @SerializedName("current_password")
    val currentPassword: String?,
    @SerializedName("new_password")
    val newPassword: String?
)