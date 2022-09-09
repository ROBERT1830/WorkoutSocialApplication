package com.robertconstantin.repository.subscription

import com.robertconstantin.data.Post
import org.litote.kmongo.and
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import org.litote.kmongo.setValue

class SubscriptionImpl(
    private val db: CoroutineDatabase
): Subscription {
    private val subscription = db.getCollection<com.robertconstantin.data.Subscription>()
    private val posts = db.getCollection<Post>()
    override suspend fun subscribeUser(currentUserId: String, postId: String): Boolean {
        posts.findOneById(postId)?.also {
            posts.updateOneById(
                id = postId,
                update = setValue(Post::subscriptionsCount, it.likeCount + 1)
            )
        } ?: return false

        return subscription.insertOne(
            com.robertconstantin.data.Subscription(userId = currentUserId, postId = postId)
        ).wasAcknowledged()
    }

    override suspend fun unSubscribeUser(currentUserId: String, postId: String): Boolean {
        posts.findOneById(postId)?.also {
            posts.updateOneById(
                id = postId,
                update = setValue(Post::subscriptionsCount, (it.likeCount - 1).coerceAtLeast(0))
                // TODO: 17/8/22 coerceAtLeast 0
            )
        } ?: return false

        return subscription.deleteOne(
            and(
                com.robertconstantin.data.Subscription::userId eq currentUserId,
                com.robertconstantin.data.Subscription::postId eq postId
            )
        ).wasAcknowledged()
    }
}