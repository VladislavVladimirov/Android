package com.netology.nmedia.repository.post

import androidx.paging.PagingData
import com.netology.nmedia.dto.Post
import com.netology.nmedia.enums.AttachmentType
import kotlinx.coroutines.flow.Flow
import java.io.InputStream

interface PostRepository {
    val data: Flow<PagingData<Post>>
    suspend fun likeById(id: Int)
    suspend fun dislikeById(id: Int)
    suspend fun removeById(id: Int)
    suspend fun save(post: Post)
    suspend fun saveWithAttachment(inputStream: InputStream, type: AttachmentType, post: Post)


}

