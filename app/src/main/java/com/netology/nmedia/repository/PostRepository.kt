package com.netology.nmedia.repository

import com.netology.nmedia.dto.Post
import kotlinx.coroutines.flow.Flow
import java.io.File

interface PostRepository {
    val data: Flow<List<Post>>
    suspend fun likeById(id: Int)
    suspend fun dislikeById(id: Int)
    suspend fun removeById(id: Int)
    suspend fun getAll()
    suspend fun save(post: Post)
    fun getNewerCount(id: Int): Flow<Int>
    suspend fun showAll()
    suspend fun saveWithAttachment(file: File, post: Post)

}

