package com.robertconstantin.request

data class CreatePostRequest(
    val sportType: String,
    val description: String,
    val location: String,
)