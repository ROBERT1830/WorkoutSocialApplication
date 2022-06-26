package com.robertconstantin.service.user_service

import com.robertconstantin.common.ValidationRequest
import com.robertconstantin.data.User
import com.robertconstantin.repository.user.UserRepository
import com.robertconstantin.request.CreateAccountRequest

class UserServiceImpl(private val userRepository: UserRepository) : UserService {

    //Validate request
    override fun validateRequest(request: CreateAccountRequest): ValidationRequest =
        if (request.email.isBlank() || request.name.isBlank() || request.password.isBlank())
            ValidationRequest.FieldEmpty
        else ValidationRequest.Success
    //Check if user exist in db
    override suspend fun checkIfUserEmailExists(email: String): Boolean =
        userRepository.getUserByEmail(email) != null
    //Crate a user in db
    override suspend fun createUser(user: User) {
        userRepository.createUser(user)
    }
}