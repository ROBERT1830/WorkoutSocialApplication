package com.robertconstantin.routes

import com.google.gson.Gson
import com.robertconstantin.common.ApiResponseMessages.FIELDS_BLANK
import com.robertconstantin.common.ApiResponseMessages.INVALID_CREDENTIALS
import com.robertconstantin.common.Constants.BASE_URL
import com.robertconstantin.common.Constants.PROFILE_PICTURE_PATH
import com.robertconstantin.common.ValidationRequest
import com.robertconstantin.request.CreateAccountRequest
import com.robertconstantin.request.LoginRequest
import com.robertconstantin.responses.AuthResponse
import com.robertconstantin.responses.ApiResponse
import com.robertconstantin.routes.util.RoutesEndpoints.CREATE_USER_ENDPOINT
import com.robertconstantin.routes.util.RoutesEndpoints.SIGN_IN_USER_ENDPOINT
import com.robertconstantin.routes.util.save
import com.robertconstantin.security.hashing.HashingService
import com.robertconstantin.security.hashing.SaltedHash
import com.robertconstantin.security.token.TokenClaim
import com.robertconstantin.security.token.TokenConfig
import com.robertconstantin.security.token.TokenService
import com.robertconstantin.service.user_service.UserService
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.koin.ktor.ext.inject
import java.io.File

// TODO: 26/6/22 inject with koin
fun Route.createUser(
    hashingService: HashingService,
    userService: UserService
) {
    val gson by inject<Gson>()

    route(CREATE_USER_ENDPOINT) {
        post {
//            val request = call.receiveOrNull<CreateAccountRequest>() ?: kotlin.run {
//                call.respond(HttpStatusCode.BadRequest)
//                return@post
//            }

            val multipart = call.receiveMultipart()
            var profileRequest: CreateAccountRequest? = null
            var profileImageFileName: String? = null

            multipart.forEachPart { partData ->
                when (partData) {
                    is PartData.FormItem -> {
                        if (partData.name == "profile_data") {
                            profileRequest = gson.fromJson(
                                partData.value,
                                CreateAccountRequest::class.java
                            )
                        }
                    }
                    is PartData.FileItem -> {
                        profileImageFileName = partData.save(PROFILE_PICTURE_PATH)
                    }
                    is PartData.BinaryItem -> Unit
                }
            }

            profileRequest?.let { request ->
                val saltedHash = hashingService.generatedSaltedHash(request.password)
                //Create user in database
                userService.createUser(profileImageUrl = "${BASE_URL}profile_pictures/$profileImageFileName" ,request = request, saltedHash = saltedHash)
            }.also { userWasCreated ->
                if (userWasCreated == true) {
                    call.respond(
                        message = ApiResponse<Unit>(
                            successful = true
                        )
                    )
                }else {
                    File("${PROFILE_PICTURE_PATH}/$profileImageFileName").delete()
                    call.respond(
                        message = ApiResponse<Unit>(
                            successful = false,
                            message = FIELDS_BLANK
                        )
                    )
                }
            } ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }

            //Validate empty request variables
//            when (userService.validateSignUpRequest(request)) {
//                is ValidationRequest.Success -> {
//                    if (userService.checkIfUserEmailExists(request.email)) {
//                        call.respond(
//                            message = ApiResponse<Unit>(
//                                message = USER_ALREADY_EXISTS,
//                                successful = false
//                            )
//                        )
//                        return@post
//                    }
//                    val saltedHash = hashingService.generatedSaltedHash(request.password)
//                    //Create user in database
//                    userService.createUser(request, saltedHash)
//
//
//                }
//                is ValidationRequest.FieldEmpty -> {
//                    call.respond(
//                        message = ApiResponse<Unit>(
//                            successful = false,
//                            message = FIELDS_BLANK
//                        )
//                    )
//                }
//            }
        }
    }
}

fun Route.signIn(
    userService: UserService,
    hashingService: HashingService,
    tokenService: TokenService,
    tokenConfig: TokenConfig
) {
    route(SIGN_IN_USER_ENDPOINT) {
        post {
            val request = call.receiveOrNull<LoginRequest>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }

            when (userService.validateLoginRequest(request)) {
                is ValidationRequest.Success -> {
                    userService.getUserByEmail(request.email)?.let { userFromDb ->
                        //the hashing will take the salt and prepend it to the user password, will hash it
                        //and then it will check if the result is equals to the has we stored in the db.
                        //We get it from the user we found in db.
                        hashingService.verify(
                            value = request.password,
                            saltedHash = SaltedHash(
                                //provide new instance of SaltedHash with data from db we already obtained
                                hash = userFromDb.password,
                                salt = userFromDb.salt
                            )
                        ).let { isValidPassword ->
                            if (!isValidPassword) {
                                call.respond(
                                    message = ApiResponse<Unit>(
                                        successful = false,
                                        message = INVALID_CREDENTIALS
                                    )
                                )
                                return@post
                            }
                            // if is a valid password, we know trhat the user succesfully loged in. So now in that case
                            // we want to generate a token and attatch it to the response so that the user can save it in preferences

                            val token = tokenService.generate(
                                config = tokenConfig,
                                TokenClaim(
                                    key = "userId",
                                    value = userFromDb.id
                                )
                            )

                            call.respond(
                                status = HttpStatusCode.OK,
                                message = AuthResponse(
                                    userId = userFromDb.id,
                                    token = token
                                )
                            )

                        }

                    } ?: kotlin.run {
                        //means we didnt find the user in the db because the user introduced wrong the fields
                        call.respond(
                            message = ApiResponse<Unit>(
                                successful = false,
                                message = INVALID_CREDENTIALS
                            )
                        )
                        return@post
                    }
                }

                is ValidationRequest.FieldEmpty -> {
                    call.respond(
                        message = ApiResponse<Unit>(
                            successful = false,
                            message = FIELDS_BLANK
                        )
                    )
                }
            }
            //find the user in the database, a given user try to login with

        }

    }
}

/**
 * Route ti authenticate the user. Lets say the user now sucesfully logged in. Then what we will do is to generate a token
 * and attatchh it to the response and user will save it in preferences. But what happens if they actrually relaunch the app
 * in that case they dont want to logg in again. They want to stay logged in like in instagram. So we need to attatch
 * the token to a request once the user oppens the app and check if that token is still valid. So for that
 * we will need the authenticate route here.
 * Usually behind the scences we will make a request to this route. And this route will just check if
 * the token user has saved in preferences is still a valid one
 */

fun Route.authenticate() {
    /**
     * This authenitcate bloick in ktor will make sure that the default authentication mechanisim'is used to verify if the
     * request is an authenitcated one. If there is a valid token this will automatically check all token stuff behind
     * the scences since we set up in security in jwt config.
     * If the user is now authenticate this function will be already make sure that ktor by default will respond with unauthorized status
     * code.
     * If everything succeds the server will respond with OK.
     *
     * If the user is not auth, then wee need to redirect the user to the login screen to log in and get an other token
     */
    authenticate {
        get("/api/user/authenticate") {
            call.respond(
                message = ApiResponse<Unit>(
                    successful = true
                )
            )
        }
    }
}

fun Route.getSecretInfo() {
    authenticate {
        get("secret") {
            //we will respond with the userid contained in the token
            //first get Principal which is in the security, the JWTPrinciple which is a wraper around an autheticated user
            //with that we can acces the claim where is the userId
            val principal = call.principal<JWTPrincipal>()
            //we want to get the claim of userId
            val userId = principal?.getClaim("userId", String::class)
            //if the user is authenticated the server will respond with the userId.
            call.respond(HttpStatusCode.OK, "Your userId is $userId")
        }
    }
}

