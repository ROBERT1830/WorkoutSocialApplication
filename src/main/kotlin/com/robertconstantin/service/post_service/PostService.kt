package com.robertconstantin.service.post_service

import com.robertconstantin.request.CreatePostRequest
import com.robertconstantin.responses.PostResponse

interface PostService {
    //to create a post we need a CreatePostRequest from client and a specific userId
    suspend fun createPost(userId: String, request: CreatePostRequest, imageUrl: String): Boolean
    suspend fun getAllPosts(ownUserId: String, page: Int, pageSize: Int): List<PostResponse>
}