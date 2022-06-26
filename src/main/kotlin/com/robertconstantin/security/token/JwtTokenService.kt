package com.robertconstantin.security.token

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import java.util.*

/**
 * A service in software developement is ususally a way to call a class that is stateless
 * and just provides functionality for other clases. so here we have that functionality to generate a token
 * but this clas does not keep any kind of state. so we dont have variables here or so
 * we just make sure to provide functionalituy for other clases by implementing this interface
 */
class JwtTokenService: TokenService {
    override fun generate(config: TokenConfig, vararg claims: TokenClaim): String {
        var token = JWT.create() //create a token and configure it
            .withAudience(config.audience)
            .withIssuer(config.issuer)
            .withExpiresAt(Date(System.currentTimeMillis() + config.expiresIn)) //provide the milis at which this token will expire. Tha way we will get the milis to one year from now

        //we can have potentially multiple claims like useriD, EMAIL ETC...
        claims.forEach { claim ->
            token = token.withClaim(claim.key, claim.value) //add the claim to the token
        }
        return token.sign(Algorithm.HMAC256(config.secret)) //the algorithm takes a secret to sign the token

        //This is how we generated a JWT token that will be done everytime the user logs in the app.
        // and then we will attatch that to the response when server will send that after login. And the client will
        //store that in shared pref or so.

    }

    /**
     * Hashing: every body should use that.
     * Is we take a look into the db, we see that currently the paswrod for users are stored in plain text.
     * Now lets imagin your backmned is hacked by someone and they find your db. And then inmediatly will find the passwords
     * in plai text and imediatly get all the users data an culd log in with those credentials.
     * That will be terirbel adn dont want to have that danger.
     * So what we instead do is to apply a hash to that password. A hash in the end is just a one way function that converts
     * the pasword to a very long and unreadable string. one way funciton means that if we have converted the passwrod
     * to an unreadable string with that function then we cant convert it to the original paswword because with the hash we
     * only can tranlate to one direction.
     *
     * Now you may wonder how do we get the pasword back. TAhe answer is that we dont need to get it back.
     * so we store the actuall hashed password in thge db and then as soon as the user actually logs in with
     * this credentials for example they use as pasword 123456 then server side we will hash the pasword the user engtered
     * and we compare the diferent hasshes because the value of a hashed pasword will always be the same.
     * If these hashes are the same we will know that the user entered the corrected password. And that is the way
     * that google and many comapnies dont know yor password because the jsut sotre the hash of the password.
     *
     * However hashing is not the most secure. Thew one concept needed is salting. Lets now imagine you have a supoer huge
     * user base and properly hash the paswords. And a hacket atacs the db. Will find all the hashed paswords and they will do is
     * to take a page 1000 mostly common poassword users choose and the will hassh all those pasword the most commonly
     * userd ones and comapre withthe hashes within you db and your users. So the user who uses the mos common password
     * then the hack will identify it So what we need to do is to apply a salt. This is a randomnly generated
     * string that we attatch to the apsword once we hash it. It will be  a totally different hash that the hsh related to the
     * pasword the user entered.
     *
     * so we will store the hash pasword with the salt we generated in the db and that way is a lot more secure.
     *
     */
}