package com.robertconstantin

import com.robertconstantin.plugins.*
import com.robertconstantin.security.token.TokenConfig
import io.ktor.application.*


fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {
    //Provide services for security
    val tokenConfig = TokenConfig(
        issuer = environment.config.property("jwt.issuer").getString(),
        audience = environment.config.property("jwt.audience").getString(),
        expiresIn = 1000L * 60L * 60L * 24L * 365L, //one year in milis
        secret = System.getenv("JWT_SECRET")
        //we get the secret from the enviroment variables since we dont want to put them in the config file to not land on github
    )

    configureKoin()
    configureSecurity(tokenConfig)
    configureHTTP()
    configureRouting(tokenConfig)
    //configureSockets()
    configureSerialization()
    configureMonitoring()
}
