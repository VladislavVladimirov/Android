package com.netology.nmedia.auth

import android.content.Context
import androidx.core.content.edit
import com.netology.nmedia.api.AuthApiService
import com.netology.nmedia.error.ApiError
import com.netology.nmedia.error.NetworkError
import com.netology.nmedia.error.UnknownError
import com.netology.nmedia.model.AuthModel
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppAuth @Inject constructor(
    @ApplicationContext
    private val context: Context,
) {
    private val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
    private val idKey = "id"
    private val tokenKey = "token"
    private val _authStateFlow: MutableStateFlow<AuthModel>

    init {
        val token = prefs.getString(tokenKey, null)
        val id = prefs.getInt(idKey, 0)

        _authStateFlow = if (id == 0 || token == null) {
            prefs.edit {
                clear()
            }
            MutableStateFlow(AuthModel())
        } else {
            MutableStateFlow(AuthModel(id, token))
        }

    }

    @InstallIn(SingletonComponent::class)
    @EntryPoint
    interface AppAuthEntryPoint {
        fun getApiService(): AuthApiService
    }
    val authStateFlow = _authStateFlow.asStateFlow()

    fun setUser(user: AuthModel) {
        _authStateFlow.value = user
        with(prefs.edit()) {
            putInt(idKey, user.id)
            putString(tokenKey, user.token)
            apply()
        }

    }

    fun removeUser() {
        _authStateFlow.value = AuthModel()
        with(prefs.edit()) {
            clear()
            apply()
        }

    }

    suspend fun signIn(login: String, pass: String) {
        try {
            val entryPoint = EntryPointAccessors.fromApplication(context, AppAuthEntryPoint::class.java)
            val response = entryPoint.getApiService().updateUser(login, pass)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            setUser(requireNotNull(response.body()))

        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    suspend fun signUp(login: String, pass: String, name: String) {
        try {
            val entryPoint = EntryPointAccessors.fromApplication(context, AppAuthEntryPoint::class.java)
            val response = entryPoint.getApiService().registerUser(login, pass, name)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            setUser(requireNotNull(response.body()))

        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    suspend fun signUpWithAvatar(login: String, pass: String, name: String, file: File) {
        try {
            val entryPoint = EntryPointAccessors.fromApplication(context, AppAuthEntryPoint::class.java)
            val part = MultipartBody.Part.createFormData("file", file.name, file.asRequestBody())
            val response = entryPoint.getApiService().registerWithPhoto(
                login.toRequestBody(),
                pass.toRequestBody(),
                name.toRequestBody(),
                part
            )
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            setUser(requireNotNull(response.body()))

        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            e.printStackTrace()
            throw UnknownError
        }
    }

}


