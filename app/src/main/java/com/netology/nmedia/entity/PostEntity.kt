package com.netology.nmedia.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.netology.nmedia.dto.Post

@Entity
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    var author: String,
    val content: String,
    var published: Long,
    val likedByMe: Boolean,
    val likes: Int = 0,
    val shares: Int = 0,
    val views: Int = 0,
    var authorAvatar: String?
) {
    fun toDto() =
        Post(id, author, content, published, likedByMe, likes, shares, views, authorAvatar)

    companion object {
        fun fromDto(dto: Post) =
            PostEntity(
                dto.id,
                dto.author,
                dto.content,
                dto.published,
                dto.likedByMe,
                dto.likes,
                dto.shares,
                dto.views,
                dto.authorAvatar
            )
    }
}