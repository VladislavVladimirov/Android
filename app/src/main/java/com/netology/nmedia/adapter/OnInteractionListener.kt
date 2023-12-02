package com.netology.nmedia.adapter

import com.netology.nmedia.dto.Event
import com.netology.nmedia.dto.Job
import com.netology.nmedia.dto.Post

interface OnInteractionListener {
    fun onPostLike(post: Post) {}
    fun onPostShare(post: Post) {}
    fun onPostRemove(post: Post) {}
    fun onPostEdit(post: Post) {}
    fun onYouTubePlay(post: Post) {}
    fun onAudioPlay(post: Post){}
    fun onVideoPlay(post: Post){}
    fun onImageClick(post:Post) {}
    fun onAuthorClick(post: Post){}

    fun onJobEdit(job: Job){}
    fun onJobRemove(job: Job) {}

    fun onEventEdit(event:Event) {}
    fun onEventRemove(event: Event){}
    fun onEventLike(event: Event) {}
    fun onEventTakePart(event: Event) {}
    fun onEventShare(event: Event) {}
    fun onYouTubePlay(event: Event) {}
    fun onImageClickEvent(event: Event) {}
    fun onAuthorClickEvent(event: Event){}
    fun onAudioPlay(event: Event){}
    fun onVideoPlay(event: Event) {}
}