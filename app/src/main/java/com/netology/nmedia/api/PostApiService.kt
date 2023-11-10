package com.netology.nmedia.api


import com.netology.nmedia.dto.Media
import com.netology.nmedia.dto.Post
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path


interface PostApiService {
    @GET("posts")
    suspend fun getPosts(): Response<List<Post>>

    @POST("posts")
    suspend fun save(@Body post: Post): Response<Post>

    @POST("posts/{id}/likes")
    suspend fun likeById(@Path("id") id: Int): Response<Post>

    @DELETE("posts/{id}/likes")
    suspend fun dislikeById(@Path("id") id: Int): Response<Post>

    @DELETE("posts/{id}")
    suspend fun removeById(@Path("id") id: Int): Response<Unit>

    @GET("posts/{id}/newer")
    suspend fun getNewer(@Path("id") id: Int): Response<List<Post>>

    @Multipart
    @POST("media")
    suspend fun upload(@Part part: MultipartBody.Part): Response<Media>

}





