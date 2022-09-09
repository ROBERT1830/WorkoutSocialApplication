package com.robertconstantin.repository.post

import com.robertconstantin.common.Constants.DEFAULT_PROFILE_PICTURE_PATH
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
) : PostRepository {

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
                    profileImage = dbPostUser?.profileImageUrl ?: DEFAULT_PROFILE_PICTURE_PATH,
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
                            Subscription::postId eq dbPost.id,
                            Subscription::userId eq currentUserId
                        )
                    ) != null
                )
                //to not show current (who perform the call) user posts
            }.filter {
                it.userId != currentUserId
            }
    }

    override suspend fun getAllCurrentUserPosts(currentUserId: String, page: Int, pageSize: Int): List<PostResponse> {
        val currentUser = userCollection.findOneById(currentUserId)
        return postCollection.find(Post::userId eq currentUserId)
            .skip(page * pageSize)
            .limit(pageSize)
            .descendingSort(Post::timestamp)
            .toList()
            .map { dbPost ->

                PostResponse(
                    postId = dbPost.id,
                    //userId who created the post
                    userId = currentUser?.id ?: "",
                    userName = currentUser?.name ?: "",
                    imageUrl = dbPost.imageUrl,
                    profileImage = currentUser?.profileImageUrl ?: DEFAULT_PROFILE_PICTURE_PATH,
                    sportType = dbPost.sportType,
                    description = dbPost.description,
                    location = dbPost.location,
                    subscriptionsCount = dbPost.subscriptionsCount,
                    likeCount = dbPost.likeCount,
                    null,
                    null
                )
            }
    }



    override suspend fun getPostById(currentUserId: String, postId: String): PostResponse? {
        val post = postCollection.findOneById(postId) ?: return null
        val user = userCollection.findOneById(post.userId) ?: return null
        return PostResponse(
            postId = postId,
            userId = user.id,
            userName = user.name,
            imageUrl = post.imageUrl,
            profileImage = user.profileImageUrl,
            description = post.description,
            sportType = post.sportType,
            location = post.location,
            subscriptionsCount = post.subscriptionsCount,
            isUserSubscribed = subscriptionCollection.findOne(
                and(Subscription::postId eq postId, Subscription::userId eq currentUserId)) != null,
            isAddedToFavorites = favoritesCollection.findOne(
                and(Favorites::postId eq post.id, Favorites::userId eq currentUserId)
            ) != null,

        )
    }

    override suspend fun deletePostById(postId: String): Boolean {

        println("----> potId: $postId")
        val postToDelete = postCollection.findOneById(postId)
        println("----> postToDelete: $postToDelete")
        val isPostDeleted = postCollection.deleteOneById(postToDelete?.id ?: "").wasAcknowledged()

        return isPostDeleted
    }

    override suspend fun createFavoriteRelation(currentUserId: String, postId: String): Boolean {
        return favoritesCollection.insertOne(Favorites(currentUserId, postId)).wasAcknowledged()
    }

    override suspend fun deleteFavoriteRelation(currentUserId: String, postId: String): Boolean {
        return favoritesCollection.deleteOne(
            and(
                Favorites::userId eq currentUserId, Favorites::postId eq postId
            )
        ).wasAcknowledged()
    }
}