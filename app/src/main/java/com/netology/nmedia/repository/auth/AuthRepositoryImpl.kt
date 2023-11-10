package com.netology.nmedia.repository.auth

import com.netology.nmedia.api.AuthApiService
import com.netology.nmedia.auth.AppAuth
import com.netology.nmedia.error.ApiError
import com.netology.nmedia.error.NetworkError
import com.netology.nmedia.error.UnknownError
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.IOException
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val appAuth: AppAuth,
    private val apiService: AuthApiService
) : AuthRepository {
    override suspend fun signIn(login: String, pass: String) {
        try {
            val response = apiService.updateUser(login, pass)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            appAuth.setUser(requireNotNull(response.body()))

        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun signUp(login: String, pass: String, name: String) {
        try {
            val response = apiService.registerUser(login, pass, name)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            appAuth.setUser(requireNotNull(response.body()))

        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun signUpWithAvatar(login: String, pass: String, name: String, file: File) {
        try {
            val part = MultipartBody.Part.createFormData(
                "file.png",
                file.name,
                file.asRequestBody()
            )
            val response = apiService.registerWithPhoto(
                login,
                pass,
                name,
                part
            )
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            appAuth.setUser(requireNotNull(response.body()))

        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

}