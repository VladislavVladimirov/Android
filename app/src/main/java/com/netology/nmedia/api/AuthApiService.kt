package com.netology.nmedia.api

import com.netology.nmedia.model.AuthModel
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

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

    @FormUrlEncoded
    @Multipart
    @POST("users/registration")
    suspend fun registerWithPhoto(
        @Field("login") login: String,
        @Field("password") password: String,
        @Field("name") name: String,
        @Part file: MultipartBody.Part,
    ): Response<AuthModel>
}