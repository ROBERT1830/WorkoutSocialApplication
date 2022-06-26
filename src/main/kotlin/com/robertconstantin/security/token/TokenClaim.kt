package com.robertconstantin.security.token

/**
 * Int he end a claim is just a key value pair. That is used to store information in a token.
 * We have therefore a name and a value. Fro example the name or key is userId and the value is whatever the user id is.
 */
data class TokenClaim(
    val key: String,
    val value: String
)
