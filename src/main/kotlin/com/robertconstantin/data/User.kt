package com.robertconstantin.data

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class User(
    val profileImageUrl: String,
    val name: String,
    val email: String,
    val password: String,
    val postCount: Long = 0,
    val salt: String,
    @BsonId
    val id: String = ObjectId().toString()
)
