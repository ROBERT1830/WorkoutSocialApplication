package com.robertconstantin.plugins

import com.robertconstantin.routes.*
import com.robertconstantin.security.hashing.HashingService
import com.robertconstantin.security.token.TokenConfig
import com.robertconstantin.security.token.TokenService
import com.robertconstantin.service.post_service.PostService
import com.robertconstantin.service.subscription_service.SubscriptionService
import com.robertconstantin.service.user_service.UserService
import io.ktor.application.*
import io.ktor.http.content.*
import io.ktor.routing.*
import org.koin.ktor.ext.inject

fun Application.configureRouting(tokenConfig: TokenConfig) {

    //Provide dependencies for routing with Koin (services)
    val userService: UserService by inject()
    val hashingService: HashingService by inject()
    val tokenService: TokenService by inject()
    val postService: PostService by inject()
    val subscriptionService: SubscriptionService by inject()

    routing {
        //User
        createUser(hashingService, userService)
        signIn(userService, hashingService,tokenService, tokenConfig )
        getUserCredentials(userService)
        updateUserCretentials(userService)
        authenticate()
        getSecretInfo()
        //Post
        createPost(postService)
        getAllPosts(postService)
        deletePostById(postService)
        createFavoriteCollection(postService)
        deleteFavoriteCollection(postService)
        getAllCurrentUserPosts(postService)
        //Subscription
        subscribe(subscriptionService)
        unsubscribe(subscriptionService)
        getPostById(postService)



        // Access static resources
        static {
            resources("static")
        }
    }
}
