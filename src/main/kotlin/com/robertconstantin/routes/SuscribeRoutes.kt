package com.robertconstantin.routes

import com.robertconstantin.common.ApiResponseMessages.USER_NOT_SUBSCRIBED
import com.robertconstantin.request.SubscriptionRequest
import com.robertconstantin.responses.ApiResponse
import com.robertconstantin.routes.util.userId
import com.robertconstantin.service.subscription_service.SubscriptionService
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.subscribe(
    subscriptionService: SubscriptionService
) {
    authenticate {
        post("api/subscribe") {
            val request = call.receiveOrNull<SubscriptionRequest>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }

            subscriptionService.subscribeUser(call.userId, request.postId).let {
                when(it) {
                    true -> {
                        call.respond(
                            status = HttpStatusCode.OK,
                            ApiResponse<Unit>(successful = true)
                        )
                    }
                    false -> {
                        call.respond(
                            status = HttpStatusCode.OK,
                            ApiResponse<Unit>(successful = false, message = USER_NOT_SUBSCRIBED)
                        )
                    }
                }
            }

        }
    }
}

fun Route.unsubscribe(
    subscriptionService: SubscriptionService
) {
    authenticate {
        post("api/unSubscribe") {
            val request = call.receiveOrNull<SubscriptionRequest>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }

            subscriptionService.unSubscribeUser(call.userId, request.postId).let {
                when(it) {
                    true -> {
                        call.respond(
                            status = HttpStatusCode.OK,
                            ApiResponse<Unit>(successful = true)
                        )
                    }
                    false -> {
                        call.respond(
                            status = HttpStatusCode.OK,
                            ApiResponse<Unit>(successful = false, message = USER_NOT_SUBSCRIBED)
                        )
                    }
                }
            }

        }
    }
}