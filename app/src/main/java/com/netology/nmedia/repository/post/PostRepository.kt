package com.netology.nmedia.repository.post

import androidx.paging.PagingData
import com.netology.nmedia.dto.Post
import kotlinx.coroutines.flow.Flow
import java.io.File

interface PostRepository {
    val data: Flow<PagingData<Post>>
    suspend fun likeById(id: Int)
    suspend fun dislikeById(id: Int)
    suspend fun removeById(id: Int)
    suspend fun save(post: Post)
    suspend fun saveWithAttachment(file: File, post: Post)



}

