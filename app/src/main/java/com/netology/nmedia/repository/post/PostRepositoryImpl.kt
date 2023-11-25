package com.netology.nmedia.repository.post

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.netology.nmedia.api.PostApiService
import com.netology.nmedia.dao.PostDao
import com.netology.nmedia.dao.PostKeyDao
import com.netology.nmedia.db.AppDb
import com.netology.nmedia.dto.Attachment
import com.netology.nmedia.dto.Media
import com.netology.nmedia.dto.Post
import com.netology.nmedia.entity.PostEntity
import com.netology.nmedia.enums.AttachmentType
import com.netology.nmedia.error.ApiError
import com.netology.nmedia.error.NetworkError
import com.netology.nmedia.error.UnknownError
import com.netology.nmedia.paging.PostRemoteMediator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.IOException
import javax.inject.Inject


class PostRepositoryImpl @Inject constructor(
    private val postDao: PostDao,
    private val apiService: PostApiService,
    postKeyDao: PostKeyDao,
    appDb: AppDb
) : PostRepository {

    @OptIn(ExperimentalPagingApi::class)
    override val data: Flow<PagingData<Post>> = Pager(
        config = PagingConfig(pageSize = 10),
        pagingSourceFactory = { postDao.getPagingSource() },
        remoteMediator = PostRemoteMediator(apiService, postDao, postKeyDao, appDb)
    ).flow
        .map { it.map(PostEntity::toDto) }



    override suspend fun save(post: Post) {
        try {
            val response = apiService.save(post)
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


    override suspend fun likeById(id: Int) {
        try {
            val response = apiService.likeById(id)
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            postDao.insert(PostEntity.fromDto(body.copy(likedByMe = true)))
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
            postDao.insert(PostEntity.fromDto(body.copy(likedByMe = false)))
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
            postDao.insert(PostEntity.fromDto(body))
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
