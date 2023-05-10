package com.netology.nmedia.repository

import androidx.lifecycle.map
import com.netology.nmedia.dao.PostDao
import com.netology.nmedia.dto.Post
import com.netology.nmedia.entity.PostEntity

class PostRepositoryImpl(
    private val dao: PostDao,
) : PostRepository {
    override fun getAll() = dao.getAll().map { list ->
        list.map {
            it.toDto()
        }
    }

    override fun likeById(id: Long) {
        dao.likeById(id)
    }

    override fun save(post: Post) {
        dao.save(PostEntity.fromDto(post))
    }

    override fun removeById(id: Long) {
        dao.removeById(id)
    }

    override fun viewPostById(id: Long) {
        dao.viewPostById(id)
    }

    override fun shareById(id: Long) {
       dao.shareById(id)
    }
}