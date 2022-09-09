package com.robertconstantin.repository.user

import com.robertconstantin.data.User
import com.robertconstantin.request.UpdateCredentialRequest
import com.robertconstantin.responses.UserCredentialResponse
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq

class UserRepositoryImpl(private val database: CoroutineDatabase): UserRepository {
    //Define collections to be userd
    private val userCollection = database.getCollection<User>()


    override suspend fun getUserByEmail(email: String): User? {
        return userCollection.findOne(User::email eq email)
    }

    override suspend fun createUser(user: User) =  userCollection.insertOne(user).wasAcknowledged()

    override suspend fun updateUserCredentials(
        currentUserId: String,
        updateCredentialRequest: UpdateCredentialRequest
    ): Boolean {
        val currentUser = userCollection.findOneById(currentUserId) ?: return false
        return userCollection.updateOneById(
            id = currentUserId,
            update = User(
                id = currentUser.id,
                profileImageUrl = currentUser.profileImageUrl,
                name = updateCredentialRequest.name,
                password = currentUser.password,
                email = updateCredentialRequest.email,
                postCount = currentUser.postCount,
                salt = currentUser.salt,
            )
        ).wasAcknowledged()
    }

    override suspend fun getUserCredentials(currentUserId: String): UserCredentialResponse? {
        val user = userCollection.findOneById(currentUserId) ?: return null
        return UserCredentialResponse(user.name, user.email)
    }
}