package com.robertconstantin.repository.post

import com.robertconstantin.data.Post
import com.robertconstantin.data.User
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.inc

class PostRepositoryImpl(
    db: CoroutineDatabase
): PostRepository {

    private val postCollection = db.getCollection<Post>()
    private val userCollection = db.getCollection<User>()

    override suspend fun createPost(post: Post): Boolean {
        //insert a post collection and then increment post count from user collection by one.
        return postCollection.insertOne(post).wasAcknowledged().also { wasAcknowledged ->
            if (wasAcknowledged) {
                userCollection.updateOneById(
                    post.userId,
                    inc(User::postCount, 1)
                )
            }
        }
    }
}