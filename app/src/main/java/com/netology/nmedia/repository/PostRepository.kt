package com.netology.nmedia.repository

import androidx.lifecycle.LiveData
import com.netology.nmedia.dto.Post

interface PostRepository {
    val data: LiveData<List<Post>>
    suspend fun likeById(id: Long)
    suspend fun dislikeById(id: Long)
    suspend fun removeById(id: Long)
    suspend fun getAll()
    suspend fun save(post: Post)
}

