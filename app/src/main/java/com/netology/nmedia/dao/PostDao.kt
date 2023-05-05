package com.netology.nmedia.dao

import com.netology.nmedia.dto.Post

interface PostDao {
    fun getAll(): List<Post>
    fun likeById(id: Long)
    fun shareById(id: Long)
    fun removeById(id: Long)
    fun save(post: Post): Post
    fun viewPostById(id: Long)
    fun saveDraft(content: String): String
    fun getDraft(): String?

}