package com.netology.nmedia.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.netology.nmedia.api.PostApiService
import com.netology.nmedia.dao.PostDao
import com.netology.nmedia.dao.PostKeyDao
import com.netology.nmedia.db.AppDb
import com.netology.nmedia.entity.PostEntity
import com.netology.nmedia.entity.PostKeyEntity
import com.netology.nmedia.entity.toPostEntity
import com.netology.nmedia.error.ApiError
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class PostRemoteMediator(
    private val apiService: PostApiService,
    private val postDao: PostDao,
    private val postKeyDao: PostKeyDao,
    private val appDb: AppDb,
) : RemoteMediator<Int, PostEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, PostEntity>
    ): MediatorResult {
        try {
            val response = when (loadType) {
                LoadType.REFRESH -> {
                    val id = postKeyDao.max()
                    if (id == null) {
                        apiService.getLatestPosts(state.config.initialLoadSize)
                    } else {
                        apiService.getPostsAfter(id, state.config.pageSize)
                    }
                }

                LoadType.PREPEND -> {
                    return MediatorResult.Success(
                        endOfPaginationReached = false
                    )
                }

                LoadType.APPEND -> {
                    val id = postKeyDao.min() ?: return MediatorResult.Success(false)
                    apiService.getPostsBefore(id, state.config.pageSize)
                }
            }

            if (!response.isSuccessful) throw ApiError(response.code(), response.message())
            val body = response.body() ?: throw ApiError(response.code(), response.message())

            if (body.isEmpty()) return MediatorResult.Success(
                endOfPaginationReached = true
            )

            appDb.withTransaction {
                when (loadType) {
                    LoadType.REFRESH -> {
                        if (postDao.isEmpty()) {
                            postKeyDao.insert(
                                listOf(
                                    PostKeyEntity(
                                        type = PostKeyEntity.KeyType.AFTER,
                                        id = body.first().id,
                                    ),
                                    PostKeyEntity(
                                        type = PostKeyEntity.KeyType.BEFORE,
                                        id = body.last().id,
                                    ),
                                )
                            )
                        } else {
                            postKeyDao.insert(
                                PostKeyEntity(
                                    type = PostKeyEntity.KeyType.AFTER,
                                    id = body.first().id,
                                )
                            )
                        }
                    }
                    LoadType.APPEND ->  postKeyDao.insert(
                        PostKeyEntity(
                            type = PostKeyEntity.KeyType.BEFORE,
                            id = body.last().id,
                        )
                    )
                    else -> {}
                }
                postDao.insert(body.toPostEntity())
            }
            return MediatorResult.Success(body.isEmpty())
        } catch (e: IOException) {
            return MediatorResult.Error(e)
        }
    }


}