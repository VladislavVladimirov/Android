package com.netology.nmedia.dto

data class Post(
    val id: Int,
    val authorId: Int,
    val author: String,
    val authorAvatar: String? = null,
    val authorJob: String? = null,
    val content: String,
    var published: String,
    val coords: Coordinates? = null,
    var link: String? = null,
    var likeOwnerIds: List<Int> = emptyList(),
    val mentionIds: List<Int> = emptyList(),
    val mentionedMe: Boolean = false,
    val likedByMe: Boolean = false,
    val attachment: Attachment? = null,
    val ownedByMe: Boolean = false,
    val users: Map<String, UserPreview> = emptyMap(),
    )
