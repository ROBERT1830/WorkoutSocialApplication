package com.robertconstantin.routes

import com.google.gson.Gson
import com.robertconstantin.common.Constants.BASE_URL
import com.robertconstantin.common.Constants.DEFAULT_PAGE_SIZE
import com.robertconstantin.common.Constants.PARAM_PAGE
import com.robertconstantin.common.Constants.PARAM_PAGE_SIZE
import com.robertconstantin.common.Constants.POST_PICTURE_PATH
import com.robertconstantin.request.CreatePostRequest
import com.robertconstantin.responses.ApiResponse
import com.robertconstantin.routes.util.RoutesEndpoints.CREATE_POST_ENDPOINT
import com.robertconstantin.routes.util.RoutesEndpoints.GET_ALL_CURRENT_USER_POSTS
import com.robertconstantin.routes.util.RoutesEndpoints.GET_ALL_POSTS
import com.robertconstantin.routes.util.save
import com.robertconstantin.routes.util.userId
import com.robertconstantin.service.post_service.PostService
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.koin.ktor.ext.inject
import java.io.File

fun Route.createPost(
    postService: PostService
) {
    val gson by inject<Gson>()

    authenticate {
        post(CREATE_POST_ENDPOINT) {
            //receive multipart data
            val multipart = call.receiveMultipart()
            var createPostRequest: CreatePostRequest? = null
            var imageFileName: String? = null
            //get post data and image
            multipart.forEachPart { partData ->
                when (partData) {
                    is PartData.FormItem -> {
                        if (partData.name == "post_data") {
                            createPostRequest = gson.fromJson(
                                partData.value,
                                CreatePostRequest::class.java
                            )
                        }
                    }
                    is PartData.FileItem -> {
                        imageFileName = partData.save(POST_PICTURE_PATH)
                    }
                    is PartData.BinaryItem -> Unit
                }
            }
            createPostRequest?.let { request ->
                postService.createPost(
                    userId = call.userId,
                    request = request,
                    imageUrl = "${BASE_URL}post_pictures/$imageFileName"
                ).also { postWasCreated ->
                    //if post was created then respond with OK
                    if (postWasCreated) {
                        call.respond(
                            status = HttpStatusCode.OK,
                            message = ApiResponse<Unit>(successful = true)
                        )
                    } else {
                        //delete the file we created and respond with internalServerError
                        File("${POST_PICTURE_PATH}/$imageFileName").delete()
                        call.respond(HttpStatusCode.InternalServerError)
                    }
                }
            } ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }
        }
    }
}

fun Route.getAllPosts(
    postService: PostService
) {
    authenticate {
        get(GET_ALL_POSTS) {
            val page = call.parameters[PARAM_PAGE]?.toIntOrNull() ?: 0
            val pageSize = call.parameters[PARAM_PAGE_SIZE]?.toIntOrNull() ?: DEFAULT_PAGE_SIZE
            call.respond(
                status = HttpStatusCode.OK,
                postService.getAllPosts(call.userId, page, pageSize)
            )

        }
    }
}

fun Route.getAllCurrentUserPosts(
    postService: PostService
) {
    authenticate {
        get(GET_ALL_CURRENT_USER_POSTS) {
            val page = call.parameters[PARAM_PAGE]?.toIntOrNull() ?: 0
            val pageSize = call.parameters[PARAM_PAGE_SIZE]?.toIntOrNull() ?: DEFAULT_PAGE_SIZE
            call.respond(
                status = HttpStatusCode.OK,
                postService.getAllCurrentUserPosts(call.userId, page, pageSize)
            )

        }
    }
}