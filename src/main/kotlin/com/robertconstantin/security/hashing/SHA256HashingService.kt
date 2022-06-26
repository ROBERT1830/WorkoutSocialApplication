package com.robertconstantin.security.hashing

import com.mongodb.internal.HexUtils
import io.ktor.util.*
import java.security.SecureRandom

class SHA256HashingService: HashingService {
    override fun generatedSaltedHash(value: String, saltLength: Int): SaltedHash {
        //generate the salt. SecureRandom is a secure way to generate secure numbers and put the algorithm to generate
        val salt = SecureRandom.getInstance("SHA1PRNG").generateSeed(saltLength) //this will generate 32 character long random string in a secure way.
        //if we take a look at salt is a bytearay so we need to store a string in db so we need to convert it to hexadecimal values
        //val saltHex = salt.encodeBase64()

        val saltAsHex = hex(salt)
        val hashBytes = getDigestFunction("SHA-256"){ saltAsHex }
        val hash = hex(hashBytes(value))

        return SaltedHash(
            hash = hash,
            salt = saltAsHex
        )

    }

    override fun verify(value: String, saltedHash: SaltedHash): Boolean {
        val hash = hex(getDigestFunction("SHA-256", salt = { saltedHash.salt })(value))
        return hash == saltedHash.hash
    }
}