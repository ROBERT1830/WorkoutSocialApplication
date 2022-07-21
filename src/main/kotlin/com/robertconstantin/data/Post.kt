package com.robertconstantin.data

data class Post(
    val userId: String,
    val imageUrl: String,
    val sportType: String,
    val description: String,
    val location: String,
    val timestamp: Long,
    val subscriptionsCount: Long = 0,
    val likeCount: Long = 0
)
