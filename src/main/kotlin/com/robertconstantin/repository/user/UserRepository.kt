package com.robertconstantin.repository.user

import com.robertconstantin.data.User
import com.robertconstantin.request.UpdateCredentialRequest
import com.robertconstantin.responses.UserCredentialResponse

interface UserRepository {
    suspend fun createUser(user: User): Boolean
    suspend fun getUserByEmail(email: String): User?

    suspend fun updateUserCredentials(currentUserId: String, updateCredentialRequest: UpdateCredentialRequest): Boolean
    suspend fun getUserCredentials(currentUserId: String): UserCredentialResponse?

}