package com.robertconstantin.di


import com.robertconstantin.Constants.DATABASE_NAME
import com.robertconstantin.repository.user.UserRepository
import com.robertconstantin.repository.user.UserRepositoryImpl
import com.robertconstantin.service.user_service.UserService
import com.robertconstantin.service.user_service.UserServiceImpl
import org.koin.core.Koin
import org.koin.dsl.module
import org.koin.logger.slf4jLogger
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

    //Provide Services
    single<UserService> { UserServiceImpl(get()) }

    //Provide Repository
    single<UserRepository> { UserRepositoryImpl(get()) }

}