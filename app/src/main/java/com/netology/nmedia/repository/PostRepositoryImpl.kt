package com.netology.nmedia.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.netology.nmedia.dto.Post
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.lang.RuntimeException
import java.util.concurrent.TimeUnit

class PostRepositoryImpl : PostRepository {
    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .build()
    private val gson = Gson()
    private val typeToken = object : TypeToken<List<Post>>() {}

    companion object {
        private const val BASE_URL = "http://192.168.0.6:9090"
        private val jsonType = "application/json".toMediaType()
    }

    override fun getAll(): List<Post> {
        val request: Request = Request.Builder()
            .url("${BASE_URL}/api/posts")
            .build()
        return client.newCall(request)
            .execute()
            .let { it.body?.string() ?: throw RuntimeException("body is null") }
            .let {
                gson.fromJson(it, typeToken.type)
            }
    }

    override fun likeById(post: Post) {
        val id = post.id
        val request: Request = if (post.likedByMe) {
            !post.likedByMe
            Request.Builder()
                .delete(gson.toJson(post).toRequestBody(jsonType))
        } else {
            !post.likedByMe
            Request.Builder()
                .post(gson.toJson(post).toRequestBody(jsonType))
        }
            .url("${BASE_URL}/api/posts/${id}/likes")
            .build()
        client.newCall(request)
            .execute()
            .close()

    }

    override fun shareById(id: Long) {

    }

    override fun viewPostById(id: Long) {

    }

    override fun save(post: Post) {

        val request: Request = Request.Builder()
            .post(gson.toJson(post).toRequestBody(jsonType))
            .url("${BASE_URL}/api/posts")
            .build()

        client.newCall(request)
            .execute()
            .close()
    }

    override fun removeById(id: Long) {
        val request: Request = Request.Builder()
            .delete()
            .url("${BASE_URL}/api/posts/$id")
            .build()

        client.newCall(request)
            .execute()
            .close()
    }

}