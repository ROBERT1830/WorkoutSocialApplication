package com.robertconstantin

import com.robertconstantin.plugins.*
import com.robertconstantin.security.hashing.SHA256HashingService
import com.robertconstantin.security.token.JwtTokenService
import com.robertconstantin.security.token.TokenConfig
import io.ktor.application.*
import org.koin.ktor.ext.inject


fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {
    //Provide services for security
    val tokenService: JwtTokenService by inject()
    val hashingService: SHA256HashingService by inject()
    val tokenConfig = TokenConfig(
        issuer = environment.config.property("jwt.issuer").getString(),
        audience = environment.config.property("jwt.audience").getString(),
        expiresIn = 1000L * 60L * 60L * 24L * 365L, //one year in milis
        secret = System.getenv("JWT_SECRET")
            //we get the secret from the enviroment variables since we dont want to put them in the config file to not land on github
    )

    val jwtIssuer = environment.config.property("jwt.domain").getString()
    val jwtAudience = environment.config.property("jwt.audience").getString()
    val jwtSecret = environment.config.property("jwt.secret").getString()

    configureKoin()
    configureSecurity(tokenConfig)
    configureHTTP()
    configureRouting()
    //configureSockets()
    configureSerialization()
    configureMonitoring()
}
