package com.example.formfit.models

data class FormFeedback(
    val message: String,
    val isNextRepFeedback: Boolean = false,
    val isGoodFeedback: Boolean = false,
    val isBadFeedback: Boolean = false,
    val isTop: Boolean = false,
    val isBottom: Boolean = false
)