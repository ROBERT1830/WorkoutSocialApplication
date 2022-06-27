package com.robertconstantin.security.token



/**
 * This interface will abstracty out how our jwt generation logic wokrs.
 * It will have a single generate function that will generate the token. This function will need
 * the tokenConfig and a variable numbre calims of type TokenClaim. So we can attatch as many claims as we like.
 * vararg works like an array of eleemnts of type TokenClaim
 * This funtiocn will return a string wich will be the generated token.
 */
interface TokenService {
    fun generate(
        config: TokenConfig,
        vararg claims: TokenClaim
    ): String
}