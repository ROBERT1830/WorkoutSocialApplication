package com.robertconstantin.service.user_service

import com.robertconstantin.common.Constants.DEFAULT_PROFILE_PICTURE_PATH
import com.robertconstantin.common.ValidationRequest
import com.robertconstantin.data.User
import com.robertconstantin.repository.user.UserRepository
import com.robertconstantin.request.CreateAccountRequest
import com.robertconstantin.request.LoginRequest
import com.robertconstantin.request.UpdateCredentialRequest
import com.robertconstantin.responses.UserCredentialResponse
import com.robertconstantin.security.hashing.SaltedHash

class UserServiceImpl(private val userRepository: UserRepository) : UserService {

    //Validate request
    override fun validateSignUpRequest(signUpRequest: CreateAccountRequest): ValidationRequest =
        if (signUpRequest.email.isBlank() || signUpRequest.name.isBlank() || signUpRequest.password.isBlank())
            ValidationRequest.FieldEmpty
        else ValidationRequest.Success

    override fun validateLoginRequest(loginRequest: LoginRequest): ValidationRequest =
        if (loginRequest.email.isBlank() || loginRequest.password.isBlank())
            ValidationRequest.FieldEmpty
        else ValidationRequest.Success

    override fun validateUserUpdateCredentials(userCredentialRequest: UpdateCredentialRequest): ValidationRequest =
        if (userCredentialRequest.email.isBlank() || userCredentialRequest.name.isBlank()) {
            ValidationRequest.FieldEmpty
        } else ValidationRequest.Success


    //Check if user exist in db
    override suspend fun checkIfUserEmailExists(email: String): Boolean =
        userRepository.getUserByEmail(email) != null

    //Crate a user in db
    override suspend fun createUser(profileImage: String, request: CreateAccountRequest, saltedHash: SaltedHash) =
        userRepository.createUser(
            User(
                profileImageUrl = profileImage,
                name = request.name,
                email = request.email,
                password = saltedHash.hash,
                salt = saltedHash.salt
            )
        )


    override suspend fun getUserByEmail(email: String): User? =
        userRepository.getUserByEmail(email)

    override suspend fun getUserCredentials(currentUserId: String): UserCredentialResponse? =
        userRepository.getUserCredentials(currentUserId)


    override suspend fun updateUserCredentials(
        currentUserId: String,
        updateCredentialRequest: UpdateCredentialRequest
    ): Boolean = userRepository.updateUserCredentials(currentUserId, updateCredentialRequest)

}