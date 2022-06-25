package com.robertconstantin.service.user_service

import com.robertconstantin.data.User
import com.robertconstantin.repository.user.UserRepository

class UserServiceImpl(private val userRepository: UserRepository): UserService {

    override suspend fun checkIfUserEmailExists(email: String): User? = userRepository.getUserByEmail(email)
}