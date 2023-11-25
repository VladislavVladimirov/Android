package com.netology.nmedia.api

import com.netology.nmedia.dto.User
import com.netology.nmedia.model.auth.AuthModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface AuthApiService {
    @FormUrlEncoded
    @POST("users/authentication")
    suspend fun updateUser(
        @Field("login") login: String,
        @Field("password") pass: String
    ): Response<AuthModel>

    @FormUrlEncoded
    @POST("users/registration")
    suspend fun registerUser(
        @Field("login") login: String,
        @Field("password") pass: String,
        @Field("name") name: String
    ): Response<AuthModel>

 
    @Multipart
    @POST("users/registration")
    suspend fun registerWithPhoto(
        @Part("login") login: RequestBody,
        @Part("password") password: RequestBody,
        @Part("name") name: RequestBody,
        @Part file: MultipartBody.Part?,
    ): Response<AuthModel>

    @GET("/api/users")
    suspend fun getUserList(): Response<List<User>>

    @GET("/api/users/{id}")
    suspend fun getUserById(@Path("id") id: Int): Response<User>
}