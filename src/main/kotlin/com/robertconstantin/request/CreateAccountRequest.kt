package com.robertconstantin.request

data class CreateAccountRequest(
    val name: String,
    val email: String,
    val password: String
)