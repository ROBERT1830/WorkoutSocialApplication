package com.robertconstantin.repository.post

import com.robertconstantin.common.Constants.DEFAULT_PAGE_SIZE
import com.robertconstantin.data.Post
import com.robertconstantin.responses.PostResponse

interface PostRepository {
    suspend fun createPost(post: Post): Boolean
    suspend fun getAllPosts(currentUserId: String, page: Int = 0, pageSize: Int = DEFAULT_PAGE_SIZE): List<PostResponse>
    suspend fun getAllCurrentUserPosts(currentUserId: String, page: Int = 0, pageSize: Int = DEFAULT_PAGE_SIZE): List<PostResponse>
    suspend fun getPostById(currentUserId: String, postId: String): PostResponse?
    suspend fun deletePostById(postId: String): Boolean
    suspend fun createFavoriteRelation(currentUserId: String, postId: String): Boolean
    suspend fun deleteFavoriteRelation(currentUserId: String, postId: String): Boolean
}