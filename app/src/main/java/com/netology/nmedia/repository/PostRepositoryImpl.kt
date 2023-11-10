package com.netology.nmedia.repository

import com.netology.nmedia.api.PostApiService
import com.netology.nmedia.dao.PostDao
import com.netology.nmedia.dto.Attachment
import com.netology.nmedia.dto.Media
import com.netology.nmedia.dto.Post
import com.netology.nmedia.entity.PostEntity
import com.netology.nmedia.entity.toDto
import com.netology.nmedia.entity.toEntity
import com.netology.nmedia.enums.AttachmentType
import com.netology.nmedia.error.ApiError
import com.netology.nmedia.error.AppError
import com.netology.nmedia.error.NetworkError
import com.netology.nmedia.error.UnknownError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.IOException
import javax.inject.Inject


class PostRepositoryImpl @Inject constructor(
    private val postDao: PostDao,
    private val apiService: PostApiService,
) : PostRepository {
    override val data = postDao.getAll().map(List<PostEntity>::toDto).flowOn(Dispatchers.Default)

    override suspend fun getAll() {
        try {
            val response = apiService.getPosts()
            if (!response.isSuccessful) throw ApiError(response.code(), response.message())
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            postDao.insert(body.toEntity(visibility = true))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override fun getNewerCount(id: Int) = flow {
        while (true) {
            delay(10000)
            val response = apiService.getNewer(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: emptyList()
            postDao.insert(body.toEntity(visibility = false))
            emit(body.size)
        }
    }
        .catch { e -> throw AppError.from(e) }
        .flowOn(Dispatchers.Default)

    override suspend fun save(post: Post) {
        try {
            val response = apiService.save(post)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            postDao.insert(PostEntity.fromDto(body, visibility = true))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }


    override suspend fun likeById(id: Int) {
        try {
            val response = apiService.likeById(id)
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            postDao.insert(PostEntity.fromDto(body.copy(likedByMe = true), visibility = true))
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun dislikeById(id: Int) {
        try {
            val response = apiService.dislikeById(id)
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            postDao.insert(PostEntity.fromDto(body.copy(likedByMe = false), visibility = true))
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun removeById(id: Int) {
        try {
            postDao.removeById(id)
            val response = apiService.removeById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun showAll() {
        try {
            postDao.showAll()
        } catch (e: ApiError) {
            throw e
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun saveWithAttachment(file: File, post: Post) {
        try {
            val media = upload(file)
            val response = apiService.save(
                post.copy(
                    attachment = Attachment(
                        url = media.url,
                        type = AttachmentType.IMAGE
                    )
                )
            )
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            postDao.insert(PostEntity.fromDto(body, visibility = true))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }


    private suspend fun upload(file: File): Media {
        val part = MultipartBody.Part.createFormData(
            "file",
            file.name,
            file.asRequestBody()
        )
        val response = apiService.upload(part)
        return response.body() ?: throw ApiError(response.code(), response.message())
    }
}
