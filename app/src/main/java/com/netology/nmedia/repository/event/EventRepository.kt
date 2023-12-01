package com.netology.nmedia.repository.event

import androidx.paging.PagingData
import com.netology.nmedia.dto.Event
import kotlinx.coroutines.flow.Flow
import java.io.File

interface EventRepository {
    val data: Flow<PagingData<Event>>
    suspend fun likeById(id: Int)
    suspend fun dislikeById(id: Int)
    suspend fun removeById(id: Int)
    suspend fun save(event: Event)
    suspend fun saveWithAttachment(file: File, event: Event)
    suspend fun takePartAtEvent(id:Int)
    suspend fun deleteTakingPart(id:Int)
}