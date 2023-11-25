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
                    apiService.getLatestPosts(state.config.initialLoadSize)

                }

                LoadType.PREPEND -> {
                    val id = postKeyDao.max() ?: return MediatorResult.Success(false)
                    apiService.getPostsAfter(id, state.config.pageSize)
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
                        postKeyDao.removeAll()
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
                        postDao.removeAll()
                    }

                    LoadType.PREPEND -> postKeyDao.insert(
                        PostKeyEntity(
                            type = PostKeyEntity.KeyType.AFTER,
                            id = body.first().id,
                        )
                    )

                    LoadType.APPEND -> postKeyDao.insert(
                        PostKeyEntity(
                            type = PostKeyEntity.KeyType.BEFORE,
                            id = body.last().id,
                        )
                    )
                }
                postDao.insert(body.toPostEntity())
            }
            return MediatorResult.Success(body.isEmpty())
        } catch (e: IOException) {
            return MediatorResult.Error(e)
        }
    }


}