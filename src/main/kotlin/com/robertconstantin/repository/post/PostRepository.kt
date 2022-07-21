package com.robertconstantin.repository.post

import com.robertconstantin.data.Post

interface PostRepository {
    suspend fun createPost(post: Post): Boolean
}