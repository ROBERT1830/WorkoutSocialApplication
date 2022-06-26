package com.robertconstantin.service.user_service

import com.robertconstantin.common.ValidationRequest
import com.robertconstantin.data.User
import com.robertconstantin.request.CreateAccountRequest

interface UserService {
    fun validateRequest(request: CreateAccountRequest): ValidationRequest
    suspend fun checkIfUserEmailExists(email: String): Boolean
    suspend fun createUser(user: User)

}