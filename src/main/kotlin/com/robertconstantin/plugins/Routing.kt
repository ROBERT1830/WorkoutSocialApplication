package com.robertconstantin.plugins

import com.robertconstantin.routes.authenticate
import com.robertconstantin.routes.createUser
import com.robertconstantin.routes.getSecretInfo
import com.robertconstantin.routes.signIn
import com.robertconstantin.security.hashing.HashingService
import com.robertconstantin.security.token.TokenConfig
import com.robertconstantin.security.token.TokenService
import com.robertconstantin.service.user_service.UserService
import io.ktor.application.*
import io.ktor.http.content.*
import io.ktor.routing.*
import org.koin.ktor.ext.inject

fun Application.configureRouting(tokenConfig: TokenConfig) {

    //Provide dependencies for routing with Koin (services)
    val userService: UserService by inject()
    val hashingService: HashingService by inject()
    val tokenService: TokenService by inject()

    routing {
        //Create user
        createUser(hashingService, userService)
        signIn(userService, hashingService,tokenService, tokenConfig )
        authenticate()
        getSecretInfo()

        // Static plugin. Try to access `/static/index.html`
        static("/static") {
            resources("static")
        }
    }
}
