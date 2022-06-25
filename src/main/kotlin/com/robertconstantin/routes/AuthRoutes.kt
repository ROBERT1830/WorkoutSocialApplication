package com.robertconstantin.routes

import com.robertconstantin.request.CreateAccountRequest
import com.robertconstantin.routes.util.RoutesEndpoints.CREATE_USER_ENDPOINT
import com.robertconstantin.service.user_service.UserService
import com.robertconstantin.service.user_service.UserServiceImpl
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*



fun Route.createUser(userServiceImpl: UserService) {

    route(CREATE_USER_ENDPOINT) {
        post {
            val request = call.receiveOrNull<CreateAccountRequest>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
            }
        }
    }

}