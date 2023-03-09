package com.netology.nmedia.repository

import androidx.lifecycle.LiveData
import com.netology.nmedia.dto.Post

interface PostRepository {
    fun get(): LiveData<Post>
    fun like()
    fun share()

}