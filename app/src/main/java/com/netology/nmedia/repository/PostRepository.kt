package com.netology.nmedia.repository

import com.netology.nmedia.dto.Post
import kotlinx.coroutines.flow.Flow
import java.io.File

interface PostRepository {
    val data: Flow<List<Post>>
    suspend fun likeById(id: Long)
    suspend fun dislikeById(id: Long)
    suspend fun removeById(id: Long)
    suspend fun getAll()
    suspend fun save(post: Post)
    fun getNewerCount(id: Long): Flow<Int>
    suspend fun showAll()
    suspend fun saveWithAttachment(file: File, post: Post)
}

