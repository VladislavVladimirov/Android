package com.netology.nmedia.dto

import com.netology.nmedia.enums.EventType

data class Event(
    val id: Int,
    val author: String,
    val authorAvatar: String? = null,
    val authorId: Int,
    val authorJob: String? = null,
    val content: String,
    val datetime: String,
    val likeOwnerIds: List<Int> = emptyList(),
    val likedByMe: Boolean = false,
    val link: String? = null,
    val ownedByMe: Boolean = false,
    val participantsIds: List<Int> = emptyList(),
    val participatedByMe: Boolean = false,
    val published: String,
    val speakerIds: List<Int> = emptyList(),
    val type: EventType,
    val attachment: Attachment? = null
)
