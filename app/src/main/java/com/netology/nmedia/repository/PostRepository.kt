package com.netology.nmedia.repository

import com.netology.nmedia.dto.Post

interface PostRepository {

    fun likeById(post:Post)
    fun shareById(id: Long)
    fun removeById(id: Long)
    fun save(post: Post)
    fun viewPostById(id: Long)
    fun getAllAsync(callback: PostsCallback<List<Post>>)

    interface PostsCallback <T> {
        fun onSuccess(data: T) {}
        fun onError(e: Exception) {}
    }



}

