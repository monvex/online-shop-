package com.example.database.images
import io.ktor.http.content.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File
import java.util.*

object Images: Table() {
    private val imageId = Images.varchar("id", 60)
    private val filename = Images.varchar("filename", 25)


    fun fetch(id: String): String{
        return transaction {
            val image = Images.selectAll().where( imageId.eq(id) ).single()
            image[filename]
        }
    }

    fun fetchAll(): List<ImageDTO> {
        val transaction = transaction {
            Images.selectAll().toList()
        }
        val imagesDTO = mutableListOf<ImageDTO>()
        transaction.forEach {
            imagesDTO.add(
                ImageDTO(
                    imageId = it[imageId],
                    fileName = it[filename]
                )
            )
        }
        return imagesDTO
    }

    fun delete(id: String) {
        val image = File(fetch(id))
        image.delete()
        transaction {
            Images.deleteWhere { imageId.eq(id) }
        }

    }

    suspend fun update(id: String, multipartData: MultiPartData): String? {
        val image = File(fetch(id))
        image.delete()
        val result = uploadImage(multipartData)
        transaction {
            Images.update({ imageId eq id }) {
                if (result != null) {
                    it[imageId] = result.imageId
                }
                if (result != null) {
                    it[filename] = result.fileName
                }

            }
        }
        return result?.imageId
    }

    suspend fun uploadImage(multipartData: MultiPartData): ImageDTO? {
        var fileName = ""
        var imageId = ""
        multipartData.forEachPart {part ->
            when (part) {
                is PartData.FileItem -> {
                    val extension = "." + part.originalFileName?.substringAfterLast('.', "")
                    fileName = "images/" + part.originalFileName?.split(".")?.get(0)?.take(14).toString() + extension
                    if (!checkExistence(fileName)) {
                        imageId = "${UUID.randomUUID()}"
                        val fileBytes = part.streamProvider().readBytes()
                        File(fileName).writeBytes(fileBytes)
                    }

                }
                else -> {}
            }
            part.dispose()
        }
        if (imageId == "") {
            return null
        } else{
            return ImageDTO(
                imageId = imageId,
                fileName = fileName
            )
        }

    }

    fun insert(id: String, fileName: String) {
        transaction {
            Images.insert {
                it[imageId] = id
                it[filename] = fileName
            }
        }
    }

    private fun checkExistence(fileName: String): Boolean {
        return try {
            transaction {
                Images.selectAll().where(Images.filename.eq(fileName)).single()
                true
            }
        } catch (e: Exception) {
            false
        }
    }
}