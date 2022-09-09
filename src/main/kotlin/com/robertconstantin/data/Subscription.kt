package com.robertconstantin.data

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class Subscription(
    val postId: String,
    val userId: String,
    @BsonId
    val id: String = ObjectId().toString()
)
