package com.robertconstantin.repository.post

import com.robertconstantin.data.Favorites
import com.robertconstantin.data.Post
import com.robertconstantin.data.Subscription
import com.robertconstantin.data.User
import com.robertconstantin.responses.PostResponse
import org.litote.kmongo.and
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import org.litote.kmongo.inc

class PostRepositoryImpl(
    db: CoroutineDatabase
): PostRepository {

    private val postCollection = db.getCollection<Post>()
    private val userCollection = db.getCollection<User>()
    private val favoritesCollection = db.getCollection<Favorites>()
    private val subscriptionCollection = db.getCollection<Subscription>()

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

    override suspend fun getAllPosts(currentUserId: String, page: Int, pageSize: Int): List<PostResponse> {
        return postCollection.find()
            .skip(page * pageSize)
            .limit(pageSize)
            .descendingSort(Post::timestamp)
            .toList()
            .map { dbPost ->
                val dbPostUser = userCollection.findOneById(dbPost.userId)
                PostResponse(
                    postId = dbPost.id,
                    //userId who created the post
                    userId = dbPostUser?.id ?: "",
                    userName = dbPostUser?.name ?: "",
                    imageUrl = dbPost.imageUrl,
                    sportType = dbPost.sportType,
                    description = dbPost.description,
                    location = dbPost.location,
                    subscriptionsCount = dbPost.subscriptionsCount,
                    likeCount = dbPost.likeCount,
                    isAddedToFavorites = favoritesCollection.findOne(
                        and(
                            Favorites::postId eq dbPost.id,
                            Favorites::userId eq currentUserId
                        )
                    ) != null,
                    isUserSubscribed = subscriptionCollection.findOne(
                        and(
                            Subscription::postId  eq dbPost.id,
                            Subscription::userId eq currentUserId
                        )
                    ) != null
                )
            }
    }
}