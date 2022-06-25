package com.robertconstantin.di


import com.robertconstantin.Constants.DATABASE_NAME
import org.koin.core.Koin
import org.koin.dsl.module
import org.koin.logger.slf4jLogger
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

val koinModule = module {
    //Create client to access mongodb based on coroutines
    val client = KMongo.createClient().coroutine
    //provide the CoroutineDb
    client.getDatabase(DATABASE_NAME)
}