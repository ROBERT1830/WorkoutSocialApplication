package com.robertconstantin.service.subscription_service

import com.robertconstantin.repository.subscription.Subscription

class SubscriptionServiceImpl(private val repository: Subscription): SubscriptionService {

    override suspend fun subscribeUser(currentUserId: String, postId: String) =
        repository.subscribeUser(currentUserId, postId)


    override suspend fun unSubscribeUser(currentUserId: String, postId: String) =
        repository.unSubscribeUser(currentUserId, postId)
}