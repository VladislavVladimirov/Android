package com.netology.nmedia.repository

import androidx.lifecycle.LiveData
import com.netology.nmedia.dto.Post

interface PostRepository {
    fun getAll(): LiveData<List<Post>>
    fun likeById(id: Long)
    fun shareById(id: Long)
    fun removeById(id: Long)
    fun save(post: Post)
    fun viewPostById(id: Long)
    fun saveDraft(content: String)
    fun getDraft(): String


}