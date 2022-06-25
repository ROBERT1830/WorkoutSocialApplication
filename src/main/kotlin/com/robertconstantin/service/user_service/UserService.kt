package com.robertconstantin.service.user_service

import com.robertconstantin.data.User

interface UserService {
    suspend fun checkIfUserEmailExists(email: String): User?
}