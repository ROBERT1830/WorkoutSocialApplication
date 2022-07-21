package com.robertconstantin.service.post_service

import com.robertconstantin.data.Post
import com.robertconstantin.repository.post.PostRepository
import com.robertconstantin.request.CreatePostRequest

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
}