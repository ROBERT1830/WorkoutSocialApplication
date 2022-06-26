package com.robertconstantin.repository.user

import com.robertconstantin.data.User
import com.robertconstantin.request.CreateAccountRequest

interface UserRepository {
    suspend fun getUserByEmail(email: String): User?
    suspend fun createUser(user: User)
}