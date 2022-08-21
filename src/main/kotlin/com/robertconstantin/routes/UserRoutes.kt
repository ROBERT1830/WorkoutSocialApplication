package com.robertconstantin.routes

import com.robertconstantin.common.ApiResponseMessages
import com.robertconstantin.common.ValidationRequest
import com.robertconstantin.request.UpdateCredentialRequest
import com.robertconstantin.responses.ApiResponse
import com.robertconstantin.routes.util.userId
import com.robertconstantin.service.user_service.UserService
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*


fun Route.getUserCredentials(userService: UserService) {
    authenticate {
        get("/api/user/credentials") {

            val response = userService.getUserCredentials(call.userId)
            if ( response != null) {
                call.respond(
                    status = HttpStatusCode.OK,
                    message = ApiResponse(
                        successful = true,
                        data = response
                    )
                )
            } else call.respond(ApiResponse<Unit>(successful = false))
        }
    }
}

fun Route.updateUserCretentials(userService: UserService) {
    authenticate {
        post("/api/user/update") {
            val request = call.receiveOrNull<UpdateCredentialRequest>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }

            when(userService.validateUserUpdateCredentials(request)) {
                is ValidationRequest.Success -> {
                    userService.updateUserCredentials(call.userId, request).also { userUpdated ->
                        if (!userUpdated) {
                            call.respond(
                                message = ApiResponse<Unit>(
                                    successful = false,
                                    message = ApiResponseMessages.USER_CREDENTIALS_NO_UPDATE
                                )
                            )
                            return@post
                        }
                        call.respond(status = HttpStatusCode.OK, message = ApiResponse<Unit>(successful = true))
                    }

                }
                is ValidationRequest.FieldEmpty -> {
                    call.respond(
                        message = ApiResponse<Unit>(
                            successful = false,
                            message = ApiResponseMessages.FIELDS_BLANK
                        )
                    )
                }
            }
        }
    }
}

