package com.robertconstantin.service.subscription_service

interface SubscriptionService {
    suspend fun subscribeUser(currentUserId: String, postId: String): Boolean
    suspend fun unSubscribeUser(currentUserId: String, postId: String): Boolean
}