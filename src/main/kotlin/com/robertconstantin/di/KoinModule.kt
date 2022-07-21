package com.robertconstantin.di


import com.google.gson.Gson
import com.robertconstantin.common.Constants.DATABASE_NAME
import com.robertconstantin.repository.post.PostRepository
import com.robertconstantin.repository.post.PostRepositoryImpl
import com.robertconstantin.repository.user.UserRepository
import com.robertconstantin.repository.user.UserRepositoryImpl
import com.robertconstantin.security.hashing.HashingService
import com.robertconstantin.security.hashing.SHA256HashingService
import com.robertconstantin.security.token.JwtTokenService
import com.robertconstantin.security.token.TokenService
import com.robertconstantin.service.post_service.PostService
import com.robertconstantin.service.post_service.PostServiceImpl
import com.robertconstantin.service.user_service.UserService
import com.robertconstantin.service.user_service.UserServiceImpl
import org.koin.dsl.module
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

val koinModule = module {
    //Provide database
    single {
        //Create client to access mongodb based on coroutines
        val client = KMongo.createClient().coroutine
        //provide the CoroutineDb
        client.getDatabase(DATABASE_NAME)
    }

    //Provide Gson
    single { Gson() }

    //Provide authentication services
    single<TokenService> { JwtTokenService() }
    single<HashingService> { SHA256HashingService() }

    //Provide Services
    single<UserService> { UserServiceImpl(get()) }
    single<PostService> {PostServiceImpl(get())}

    //Provide Repository
    single<UserRepository> { UserRepositoryImpl(get()) }
    single<PostRepository> { PostRepositoryImpl(get())}



}