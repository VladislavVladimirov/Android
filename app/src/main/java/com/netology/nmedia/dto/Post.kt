package com.netology.nmedia.dto

data class Post(
    val id: Long,
    val author: String,
    val authorId : Long,
    val content: String,
    var published: String,
    val likedByMe: Boolean,
    val likes: Int = 0,
    val shares: Int = 0,
    val views: Int = 0,
    val authorAvatar: String,
    val attachment: Attachment? = null,
    val ownedByMe: Boolean = false,
)
