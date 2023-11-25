package com.netology.nmedia.repository.user

import com.netology.nmedia.api.AuthApiService
import com.netology.nmedia.dao.UserDao
import com.netology.nmedia.dto.User
import com.netology.nmedia.entity.toDto
import com.netology.nmedia.entity.toUserEntity
import com.netology.nmedia.error.ApiError
import com.netology.nmedia.error.NetworkError
import com.netology.nmedia.error.UnknownError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val authApiService: AuthApiService,
) : UserRepository {

    override val data: Flow<List<User>> =
        userDao.getAll()
            .map { it.toDto() }
            .flowOn(Dispatchers.Default)

    override suspend fun getAll() {
        try {
            userDao.getAll()
            val response = authApiService.getUserList()
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            userDao.insert(body.toUserEntity())
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun getUserById(id: Int): User {
        try {
            val response = authApiService.getUserById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            return response.body() ?: throw ApiError(response.code(), response.message())
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }
}