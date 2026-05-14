package com.example.formfit.models

data class ChatMessage (
    val message: String,
    val createdAt: String,
    val isUser: Boolean,
    var isNew: Boolean
)