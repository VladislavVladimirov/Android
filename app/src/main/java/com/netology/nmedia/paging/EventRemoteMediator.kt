package com.netology.nmedia.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.netology.nmedia.api.EventApiService
import com.netology.nmedia.dao.EventDao
import com.netology.nmedia.dao.EventKeyDao
import com.netology.nmedia.db.AppDb
import com.netology.nmedia.entity.EventEntity
import com.netology.nmedia.entity.EventKeyEntity
import com.netology.nmedia.entity.toEventEntity
import com.netology.nmedia.error.ApiError
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class EventRemoteMediator(
    private val apiService: EventApiService,
    private val eventDao: EventDao,
    private val eventKeyDao: EventKeyDao,
    private val appDb: AppDb
) : RemoteMediator<Int, EventEntity>() {
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, EventEntity>
    ): MediatorResult {
        try {
            val response = when (loadType) {
                LoadType.REFRESH -> {
                    apiService.getEventsLatest(state.config.initialLoadSize)
                }

                LoadType.PREPEND -> {
                    val id = eventKeyDao.max() ?: return MediatorResult.Success(false)
                    apiService.getEventsAfter(id, state.config.pageSize)
                }

                LoadType.APPEND -> {
                    val id = eventKeyDao.min() ?: return MediatorResult.Success(false)
                    apiService.getEventsBefore(id, state.config.pageSize)
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
                        eventKeyDao.removeAll()
                        eventKeyDao.insert(
                            listOf(
                                EventKeyEntity(
                                    type = EventKeyEntity.KeyType.AFTER,
                                    id = body.first().id
                                ),
                                EventKeyEntity(
                                    type = EventKeyEntity.KeyType.BEFORE,
                                    id = body.last().id
                                )
                            )
                        )
                        eventDao.removeAll()
                    }

                    LoadType.PREPEND ->
                        eventKeyDao.insert(
                            EventKeyEntity(
                                type = EventKeyEntity.KeyType.AFTER,
                                id = body.first().id
                            )
                        )

                    LoadType.APPEND ->
                        eventKeyDao.insert(
                            EventKeyEntity(
                                type = EventKeyEntity.KeyType.BEFORE,
                                id = body.last().id
                            )
                        )
                }
                eventDao.insert(body.toEventEntity())
            }
            return MediatorResult.Success(body.isEmpty())
        } catch (e: IOException) {
            return MediatorResult.Error(e)
        }
    }
}
