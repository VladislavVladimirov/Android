package com.netology.nmedia.repository.event

import androidx.paging.PagingData
import com.netology.nmedia.dto.Event
import com.netology.nmedia.enums.AttachmentType
import kotlinx.coroutines.flow.Flow
import java.io.InputStream

interface EventRepository {
    val data: Flow<PagingData<Event>>
    suspend fun likeById(id: Int)
    suspend fun dislikeById(id: Int)
    suspend fun removeById(id: Int)
    suspend fun save(event: Event)
    suspend fun saveWithAttachment(inputStream: InputStream, type: AttachmentType, event: Event)
    suspend fun takePartAtEvent(id:Int)
    suspend fun deleteTakingPart(id:Int)
}