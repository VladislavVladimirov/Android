package com.netology.nmedia.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.netology.nmedia.api.PostApiService
import com.netology.nmedia.dao.WallPostDao
import com.netology.nmedia.dao.WallPostKeyDao
import com.netology.nmedia.db.AppDb
import com.netology.nmedia.entity.WallPostEntity
import com.netology.nmedia.entity.WallPostKeyEntity
import com.netology.nmedia.entity.toWallPostEntity
import com.netology.nmedia.error.ApiError


@OptIn(ExperimentalPagingApi::class)
class WallRemoteMediator(
    private val apiService: PostApiService,
    private val wallPostDao: WallPostDao,
    private val wallPostKeyDao: WallPostKeyDao,
    private val appDb: AppDb,
    private val authorId: Int,
) : RemoteMediator<Int, WallPostEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, WallPostEntity>
    ): MediatorResult {
        try {
            val response = when (loadType) {
                LoadType.REFRESH -> {
                    val id = wallPostKeyDao.max()
                    if (id == null) {
                        apiService.getLatestWallPosts(authorId, state.config.initialLoadSize)
                    } else {
                        apiService.getWallPostsAfter(id, authorId, state.config.pageSize)
                    }
                }

                LoadType.PREPEND -> {
                    return MediatorResult.Success(
                        endOfPaginationReached = false
                    )
                }

                LoadType.APPEND -> {
                    val id = wallPostKeyDao.min() ?: return MediatorResult.Success(false)
                    apiService.getWallPostsBefore(id, authorId, state.config.pageSize)
                }
            }

            if (!response.isSuccessful) throw ApiError(response.code(), response.message())
            val body = response.body() ?: throw Error(response.message())

            if (body.isEmpty()) return MediatorResult.Success(
                endOfPaginationReached = true
            )

            appDb.withTransaction {
                when (loadType) {
                    LoadType.REFRESH -> {
                        if (wallPostDao.isEmpty()) {
                            wallPostKeyDao.insert(
                                listOf(
                                    WallPostKeyEntity(
                                        type = WallPostKeyEntity.KeyType.AFTER,
                                        id = body.first().id,
                                    ),
                                    WallPostKeyEntity(
                                        type = WallPostKeyEntity.KeyType.BEFORE,
                                        id = body.last().id,
                                    ),
                                )
                            )
                        } else {
                            wallPostKeyDao.insert(
                                WallPostKeyEntity(
                                    type = WallPostKeyEntity.KeyType.AFTER,
                                    id = body.first().id,
                                )
                            )
                        }
                    }

                    LoadType.APPEND -> wallPostKeyDao.insert(
                        WallPostKeyEntity(
                            type = WallPostKeyEntity.KeyType.BEFORE,
                            id = body.last().id,
                        )
                    )

                    else -> {}

                }


                wallPostDao.insert(body.toWallPostEntity())
            }
            return MediatorResult.Success(endOfPaginationReached = body.isEmpty())
        } catch (e: Exception) {
            return MediatorResult.Error(e)
        }
    }


}
