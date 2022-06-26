package com.robertconstantin.security.token

/**
 * This data class sumarizes al the fields that we want to have for our JWT token config.
 * issuer: is who issue/emits this token which in our case is jsut the server
 * audience: coould be the normal users, the administrator etc...you can have different types of tokens for diferent
 * types of audience. Because they do diferent thinks in the app. so if you want to delete a user as an account administratorThena  normal user wont be able to do that.
 * expiresIn: Every single jwt token has an expiration date. This is an other layer of security that makes sure that
 * at some point the token expires and then someon cant log in with that token insead needs to regenerate an other tken.
 * secre: this wont be known by the cleint
 */
data class TokenConfig(
    val issuer: String,
    val audience: String,
    val expiresIn: Long,
    val secret: String
)
