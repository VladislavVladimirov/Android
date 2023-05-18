package com.netology.nmedia.dto

data class Post(
    val id: Long,
    val author: String,
    val content: String,
    var published: String,
    val likedByMe: Boolean,
    val likes: Int,
    val shares: Int,
    val views: Int,
    val authorAvatar: Int
)
