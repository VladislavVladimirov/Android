package com.netology.nmedia.repository


import androidx.lifecycle.map
import com.netology.nmedia.api.PostsApi
import com.netology.nmedia.dao.PostDao
import com.netology.nmedia.dto.Post
import com.netology.nmedia.entity.PostEntity
import com.netology.nmedia.entity.toDto
import com.netology.nmedia.entity.toEntity
import com.netology.nmedia.error.ApiError
import com.netology.nmedia.error.NetworkError
import com.netology.nmedia.error.UnknownError
import java.io.IOException


class PostRepositoryImpl(private val postDao: PostDao) : PostRepository {
    override val data = postDao.getAll().map(List<PostEntity>::toDto)

    override suspend fun getAll() {
        try {
            val response = PostsApi.retrofitService.getAll()
            if (!response.isSuccessful) throw ApiError(response.code(), response.message())
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            postDao.insert(body.toEntity())
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun save(post: Post) {
        try {
            val response = PostsApi.retrofitService.save(post)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            postDao.insert(PostEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun likeById(id: Long) {
        try {
            postDao.likeById(id)
            val response = PostsApi.retrofitService.likeById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun dislikeById(id: Long) {
        try {
            postDao.unlikeById(id)
            val response = PostsApi.retrofitService.dislikeById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun removeById(id: Long) {
        try {
            postDao.removeById(id)
            val response = PostsApi.retrofitService.removeById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }
}
