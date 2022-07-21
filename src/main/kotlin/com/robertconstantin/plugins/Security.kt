package com.robertconstantin.plugins


import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.robertconstantin.security.token.TokenConfig
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*

fun Application.configureSecurity(tokenConfig: TokenConfig) {

    authentication {
        jwt {
            //remove this because is not needed anymore cause we specified it
            //val jwtAudience = this@configureSecurity.environment.config.property("jwt.audience").getString()
            realm = this@configureSecurity.environment.config.property("jwt.realm").getString()
            verifier(
                JWT
                    .require(Algorithm.HMAC256(tokenConfig.secret))
                    .withAudience(tokenConfig.audience)
                    .withIssuer(tokenConfig.issuer)
                    .build()
            )

            validate { credential ->
                //id the credential payload audience contains tour audience then ktor will respond
                //with JWTPrincipal withc is just an authenticated user in terms of ktor
                if (credential.payload.audience.contains(tokenConfig.audience)) JWTPrincipal(credential.payload) else null
            }
        }
    }
}

//from an authenticated user get the userId.

val JWTPrincipal.userId: String?
    get() = getClaim("userId", String::class)
