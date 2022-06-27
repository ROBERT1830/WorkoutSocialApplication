package com.robertconstantin.routes

import com.robertconstantin.common.ApiResponseMessages.FIELDS_BLANK
import com.robertconstantin.common.ApiResponseMessages.USER_ALREADY_EXISTS
import com.robertconstantin.common.ValidationRequest
import com.robertconstantin.data.User
import com.robertconstantin.request.CreateAccountRequest
import com.robertconstantin.responses.BasicApiResponse
import com.robertconstantin.routes.util.RoutesEndpoints.CREATE_USER_ENDPOINT
import com.robertconstantin.security.hashing.HashingService
import com.robertconstantin.service.user_service.UserService
import com.robertconstantin.service.user_service.UserServiceImpl
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

// TODO: 26/6/22 inject with koin
fun Route.createUser(
    hashingService: HashingService,
    userService: UserService
) {

    route(CREATE_USER_ENDPOINT) {
        post {
            val request = call.receiveOrNull<CreateAccountRequest>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }
            //Validate empty request variables
            when (userService.validateRequest(request)) {
                is ValidationRequest.Success -> {
                    if (userService.checkIfUserEmailExists(request.email)) {
                        call.respond(
                            message = BasicApiResponse<Unit>(
                                message = USER_ALREADY_EXISTS,
                                successful = false
                            )
                        )
                        return@post
                    }
                    val saltedHash =  hashingService.generatedSaltedHash(request.password)
                    //Create user in database
                    userService.createUser(User(
                        name = request.name,
                        email = request.email,
                        password = saltedHash.hash,
                        salt = saltedHash.salt
                    ))
                    call.respond(
                        message = BasicApiResponse<Unit>(
                            successful = true
                        )
                    )

                }
                is ValidationRequest.FieldEmpty -> {
                    call.respond(
                        message = BasicApiResponse<Unit>(
                            successful = false,
                            message = FIELDS_BLANK
                        )
                    )
                }
            }
        }
    }
}