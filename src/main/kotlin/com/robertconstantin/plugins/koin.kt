package com.robertconstantin.plugins

import com.robertconstantin.di.koinModule
import io.ktor.application.*
import org.koin.logger.slf4jLogger

fun Application.configureKoin() {
    install(org.koin.ktor.ext.Koin){
        slf4jLogger(level = org.koin.core.logger.Level.ERROR)
        modules(koinModule)
    }
}