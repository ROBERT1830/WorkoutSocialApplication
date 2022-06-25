package com.robertconstantin.repository.user

import com.robertconstantin.data.User

interface UserRepository {
    suspend fun getUserByEmail(email: String): User?
}