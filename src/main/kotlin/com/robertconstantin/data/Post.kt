package com.robertconstantin.data

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class Post(
    val userId: String,
    val imageUrl: String,
    val sportType: String,
    val description: String,
    val location: String,
    val timestamp: Long,
    val subscriptionsCount: Long = 0,
    val likeCount: Long = 0, //no needed I think
    @BsonId
    val id: String = ObjectId().toString()
)
