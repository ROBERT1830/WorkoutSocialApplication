package com.robertconstantin.routes

import com.google.gson.Gson
import com.robertconstantin.common.Constants.BASE_URL
import com.robertconstantin.common.Constants.POST_PICTURE_PATH
import com.robertconstantin.request.CreatePostRequest
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
){
    val gson by inject<Gson>()

    authenticate {
        post("/api/post/create") {
            //receive multipart data
            val multipart = call.receiveMultipart()
            var createPostRequest: CreatePostRequest? = null
            var imageFileName: String? = null
            //get post data and image
            multipart.forEachPart { partData ->
                when(partData) {
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
            createPostRequest?.let { request->
                postService.createPost(
                    userId = call.userId,
                    request = request,
                    imageUrl = "${BASE_URL}post_pictures/$imageFileName"
                ).also { postWasCreated ->
                    //if post was created then respond with OK
                    if (postWasCreated) {
                        call.respond(HttpStatusCode.OK)
                    }else {
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