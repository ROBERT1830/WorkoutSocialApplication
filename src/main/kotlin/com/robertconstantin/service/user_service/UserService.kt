package com.robertconstantin.service.user_service

import com.robertconstantin.common.ValidationRequest
import com.robertconstantin.data.User
import com.robertconstantin.request.CreateAccountRequest
import com.robertconstantin.request.LoginRequest
import com.robertconstantin.security.hashing.SaltedHash

interface UserService {
    fun validateSignUpRequest(signUpRequest: CreateAccountRequest): ValidationRequest
    fun validateLoginRequest(loginRequest: LoginRequest): ValidationRequest
    suspend fun checkIfUserEmailExists(email: String): Boolean
    suspend fun createUser(request: CreateAccountRequest, saltedHash: SaltedHash)
    suspend fun getUserByEmail(email: String): User?

}