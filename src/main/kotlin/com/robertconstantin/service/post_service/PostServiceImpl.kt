package com.robertconstantin.service.post_service

import com.robertconstantin.data.Post
import com.robertconstantin.repository.post.PostRepository
import com.robertconstantin.request.CreatePostRequest
import com.robertconstantin.responses.PostResponse

class PostServiceImpl(
    private val repository: PostRepository
): PostService {

    override suspend fun createPost(userId: String, request: CreatePostRequest, imageUrl: String): Boolean {
        return repository.createPost(
            Post(
                userId = userId,
                imageUrl = imageUrl,
                sportType = request.sportType,
                description = request.description,
                location = request.location,
                timestamp = System.currentTimeMillis()
            )
        )
    }

    override suspend fun getAllPosts(ownUserId: String, page: Int, pageSize: Int): List<PostResponse> {
        return repository.getAllPosts(ownUserId, page, pageSize)
    }

    override suspend fun getAllCurrentUserPosts(ownUserId: String, page: Int, pageSize: Int): List<PostResponse> {
        return repository.getAllCurrentUserPosts(ownUserId, page, pageSize)
    }

    override suspend fun getPostById(currentUserId: String, postId: String): PostResponse? {
        return repository.getPostById(currentUserId, postId)
    }
}