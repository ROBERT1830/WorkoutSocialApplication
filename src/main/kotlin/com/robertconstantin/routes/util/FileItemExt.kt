package com.robertconstantin.routes.util

import io.ktor.http.content.*
import java.io.File
import java.util.*

fun PartData.FileItem.save(path: String): String {
    //read bytes from entering file
    val fileBytes = streamProvider().readBytes()
    //create file extension
    val fileExtension = originalFileName?.takeLastWhile { it != '.' }
    //create file name to be saved in db
    val fileName = UUID.randomUUID().toString() + "." + fileExtension
    //create a folder where images will be saved using the given path
    val folder = File(path)
    folder.mkdir()
    //create the image file itself and write bytes
    File("$path$fileName").writeBytes(fileBytes)

    return fileName
}