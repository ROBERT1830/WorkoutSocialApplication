package com.robertconstantin.repository.user

import com.robertconstantin.data.User
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq

class UserRepositoryImpl(private val database: CoroutineDatabase): UserRepository {
    //Define collections to be userd
    private val userCollection = database.getCollection<User>()

    override suspend fun getUserByEmail(email: String): User? {
        return userCollection.findOne(User::email eq email)
    }
}