package com.example.formfit.models

import com.google.gson.annotations.SerializedName

data class UserIssue (
    val id: Int,
    @SerializedName("user_id")
    val userId: Int,
    val type: String,
    val exercise: String,
    val location: String,
    val description: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("resolved_at")
    val resolvedAt: String
)