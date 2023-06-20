package com.netology.nmedia.repository

import com.netology.nmedia.api.ApiPosts
import com.netology.nmedia.dto.Post
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class PostRepositoryImpl : PostRepository {
    override fun getAll(callback: PostRepository.PostsCallback<List<Post>>) {
        ApiPosts.retrofitService.getAll().enqueue(object : Callback<List<Post>> {
            override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                if (!response.isSuccessful) {
                    callback.onError(Exception(response.message()))
                } else {
                    callback.onSuccess(requireNotNull(response.body()) { "body is null" })
                }
            }

            override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                callback.onError(Exception(t))
            }
        })
    }

    override fun save(post: Post, callback: PostRepository.PostsCallback<Post>) {
        ApiPosts.retrofitService.save(post).enqueue(object : Callback<Post> {
            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                if (!response.isSuccessful) {
                    callback.onError(Exception(response.message()))
                } else {
                    callback.onSuccess(
                        requireNotNull(response.body()) { "body is null" }
                    )
                }
            }

            override fun onFailure(call: Call<Post>, t: Throwable) {
                callback.onError(Exception(t))
            }
        })
    }

    override fun likeById(id: Long, callback: PostRepository.PostsCallback<Post>) {
        ApiPosts.retrofitService.likeById(id).enqueue(object : Callback<Post>{
            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                if (!response.isSuccessful) {
                    callback.onError(Exception(response.message()))
                } else {
                    callback.onSuccess(
                        requireNotNull(response.body()) { "body is null" }
                    )
                }
            }
            override fun onFailure(call: Call<Post>, t: Throwable) {
                callback.onError(Exception(t))
            }
        })
    }

    override fun dislikeById(id: Long, callback: PostRepository.PostsCallback<Post>) {
        ApiPosts.retrofitService.dislikeById(id).enqueue(object : Callback<Post>{
            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                if (!response.isSuccessful) {
                    callback.onError(Exception(response.message()))
                } else {
                    callback.onSuccess(
                        requireNotNull(response.body()) { "body is null" }
                    )
                }
            }
            override fun onFailure(call: Call<Post>, t: Throwable) {
                callback.onError(Exception(t))
            }
        })
    }

    override fun removeById(id: Long, callback: PostRepository.PostsCallback<Unit>) {
        ApiPosts.retrofitService.removeById(id).enqueue(object : Callback<Unit>{
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (!response.isSuccessful) {
                    callback.onError(Exception(response.message()))
                } else {
                    callback.onSuccess(Unit)
                }
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                callback.onError(Exception(t))
            }
        })
    }
}