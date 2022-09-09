package com.robertconstantin.repository.subscription

interface Subscription {
    suspend fun subscribeUser(currentUserId: String, postId: String): Boolean
    suspend fun unSubscribeUser(currentUserId: String, postId: String): Boolean
}