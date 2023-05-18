package com.netology.nmedia.repository

import com.netology.nmedia.dto.Post

interface PostRepository {
    fun getAll(): List<Post>
    fun likeById(post:Post)
    fun shareById(id: Long)
    fun removeById(id: Long)
    fun save(post: Post)
    fun viewPostById(id: Long)



}