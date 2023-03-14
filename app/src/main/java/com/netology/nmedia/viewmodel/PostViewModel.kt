package com.netology.nmedia.viewmodel

import androidx.lifecycle.ViewModel
import com.netology.nmedia.repository.PostRepository
import com.netology.nmedia.repository.PostRepositoryInMemoryImpl

class PostViewModel : ViewModel() {
    private val repository: PostRepository = PostRepositoryInMemoryImpl()
    val data = repository.getAll()
    fun likeById(id: Long) = repository.likeById(id)
    fun shareById(id: Long) = repository.shareById(id)

}
