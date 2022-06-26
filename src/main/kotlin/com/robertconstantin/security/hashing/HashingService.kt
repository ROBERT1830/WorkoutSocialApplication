package com.robertconstantin.security.hashing
//this only provides functionality for other files
interface HashingService {
    //value is the password that the user enter in plain text and we want to hash
    fun generatedSaltedHash(value: String, saltLength: Int = 32): SaltedHash
    //function to verify the specific hash
    fun verify(value: String, saltedHash: SaltedHash): Boolean //we will get the salltedHash from teh db. true if the user enter the correct password or false
}
