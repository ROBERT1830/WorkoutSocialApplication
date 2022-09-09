package com.robertconstantin.service.user_service

import com.robertconstantin.common.ValidationRequest
import com.robertconstantin.data.User
import com.robertconstantin.request.CreateAccountRequest
import com.robertconstantin.request.LoginRequest
import com.robertconstantin.request.UpdateCredentialRequest
import com.robertconstantin.responses.UserCredentialResponse
import com.robertconstantin.security.hashing.SaltedHash

interface UserService {
    fun validateSignUpRequest(signUpRequest: CreateAccountRequest): ValidationRequest
    fun validateLoginRequest(loginRequest: LoginRequest): ValidationRequest
    fun validateUserUpdateCredentials(userCredentialRequest: UpdateCredentialRequest): ValidationRequest

    suspend fun checkIfUserEmailExists(email: String): Boolean
    suspend fun createUser(profileImageUrl: String, request: CreateAccountRequest, saltedHash: SaltedHash): Boolean
    suspend fun getUserByEmail(email: String): User?

    suspend fun getUserCredentials(currentUserId: String): UserCredentialResponse?
    suspend fun updateUserCredentials(currentUserId: String, updateCredentialRequest: UpdateCredentialRequest): Boolean

}