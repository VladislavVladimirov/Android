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
import retrofit2.http.Query


interface PostApiService {



    @POST("posts")
    suspend fun save(@Body post: Post): Response<Post>

    @POST("posts/{id}/likes")
    suspend fun likeById(@Path("id") id: Int): Response<Post>

    @DELETE("posts/{id}/likes")
    suspend fun dislikeById(@Path("id") id: Int): Response<Post>

    @DELETE("posts/{id}")
    suspend fun removeById(@Path("id") id: Int): Response<Unit>

    @Multipart
    @POST("media")
    suspend fun upload(@Part part: MultipartBody.Part): Response<Media>
    @GET("/api/posts/latest/")
    suspend fun getLatestPosts(@Query("count") count: Int): Response<List<Post>>

    @GET("/api/posts/{post_id}/before")
    suspend fun getPostsBefore(
        @Path("post_id") id: Int,
        @Query("count") count: Int
    ): Response<List<Post>>

    @GET("/api/posts/{post_id}/after")
    suspend fun getPostsAfter(
        @Path("post_id") id: Int,
        @Query("count") count: Int
    ): Response<List<Post>>

    @GET("/api/{authorId}/wall/latest")
    suspend fun getLatestWallPosts(
        @Path("authorId") authorId: Int,
        @Query("count") count: Int
    ): Response<List<Post>>

    @GET("/api/{authorId}/wall/{id}/before")
    suspend fun getWallPostsBefore(
        @Path("id") id: Int,
        @Path("authorId") authorId: Int,
        @Query("count") count: Int
    ): Response<List<Post>>

    @GET("/api/{authorId}/wall/{id}/after")
    suspend fun getWallPostsAfter(
        @Path("id") id: Int,
        @Path("authorId") authorId: Int,
        @Query("count") count: Int
    ): Response<List<Post>>
}






