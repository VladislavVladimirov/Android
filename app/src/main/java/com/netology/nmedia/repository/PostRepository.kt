package com.netology.nmedia.repository

import com.netology.nmedia.dto.Post

interface PostRepository {

    fun likeById(id: Long, callback: PostsCallback<Post>)
    fun dislikeById(id: Long, callback: PostsCallback<Post>)
    fun removeById(id: Long, callback: PostsCallback<Unit>)
    fun save(post: Post, callback: PostsCallback<Post>)
    fun getAll(callback: PostsCallback<List<Post>>)

    interface PostsCallback <T> {
        fun onSuccess(input: T) {}
        fun onError(e: Exception) {}

    }



}

