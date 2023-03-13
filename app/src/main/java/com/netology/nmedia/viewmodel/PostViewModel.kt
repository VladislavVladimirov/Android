package com.netology.nmedia.viewmodel

import androidx.lifecycle.ViewModel
import com.netology.nmedia.repository.PostRepository
import com.netology.nmedia.repository.PostRepositoryInMemoryImpl

class PostViewModel: ViewModel() {
    private val repository: PostRepository = PostRepositoryInMemoryImpl()
    val data = repository.get()
    fun like() = repository.like()
    fun share() = repository.share()

    }
