package com.robertconstantin.plugins

import com.robertconstantin.routes.createUser
import com.robertconstantin.security.hashing.HashingService
import com.robertconstantin.security.hashing.SHA256HashingService
import com.robertconstantin.service.user_service.UserService
import io.ktor.application.*
import io.ktor.http.content.*
import io.ktor.routing.*
import org.koin.ktor.ext.inject

fun Application.configureRouting() {

    //Provide dependencies for routing with Koin (services)
    val userService: UserService by inject()
    val hashingService: HashingService by inject()


    routing {
        //Create user
        createUser(hashingService, userService)

        // Static plugin. Try to access `/static/index.html`
        static("/static") {
            resources("static")
        }
    }
}
