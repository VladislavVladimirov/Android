package com.netology.nmedia.model

import com.netology.nmedia.dto.Post

data class FeedModel(
    val posts: List<Post> = emptyList(),
    val empty: Boolean = false,
)