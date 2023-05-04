package com.netology.nmedia.adapter

import com.netology.nmedia.dto.Post

interface OnInteractionListener {
    fun onLike(post: Post) {}
    fun onShare(post: Post) {}
    fun onRemove(post: Post) {}
    fun onEdit(post: Post) {}
    fun onPlay(post: Post) {}
    fun onPostClick(post: Post) {}
}