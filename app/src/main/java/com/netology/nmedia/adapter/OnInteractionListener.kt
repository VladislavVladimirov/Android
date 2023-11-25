package com.netology.nmedia.adapter

import com.netology.nmedia.dto.Job
import com.netology.nmedia.dto.Post

interface OnInteractionListener {
    fun onPostLike(post: Post) {}
    fun onPostShare(post: Post) {}
    fun onPostRemove(post: Post) {}
    fun onPostEdit(post: Post) {}
    fun onPlay(post: Post) {}
    fun onImageClick(post:Post) {}
    fun onAuthorClick(post: Post){}
    fun onJobEdit(job: Job){}
    fun onJobRemove(job: Job) {}
}