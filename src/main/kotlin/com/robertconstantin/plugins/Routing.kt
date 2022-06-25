package com.robertconstantin.plugins

import com.robertconstantin.routes.createUser
import com.robertconstantin.service.user_service.UserService
import io.ktor.server.routing.*
import io.ktor.http.*
import io.ktor.server.http.content.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import org.koin.ktor.ext.inject

fun Application.configureRouting() {

    //Provide dependencies for routing with Koin (services)
    val userService: UserService by inject()


    routing {
        //Create user
        createUser(userService)

        // Static plugin. Try to access `/static/index.html`
        static("/static") {
            resources("static")
        }
    }
}
