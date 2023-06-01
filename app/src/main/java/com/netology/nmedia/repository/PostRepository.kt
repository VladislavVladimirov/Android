package com.netology.nmedia.repository

import com.netology.nmedia.dto.Post

interface PostRepository {

    fun likeById(post:Post, callback: PostsCallback<Post>)
    fun removeById(id: Long, callback: PostsCallback<Unit>)
    fun save(post: Post, callback: PostsCallback<Post>)
    fun getAllAsync(callback: PostsCallback<List<Post>>)

    interface PostsCallback <T> {
        fun onSuccess(input: T) {}
        fun onError(e: Exception) {}
    }



}

