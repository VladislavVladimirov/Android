package com.netology.nmedia.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.netology.nmedia.activity.enums.AttachmentType
import com.netology.nmedia.dto.Attachment
import com.netology.nmedia.dto.Post

@Entity
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val author: String,
    val authorAvatar: String,
    val content: String,
    val published: String,
    val likedByMe: Boolean,
    val likes: Int = 0,
    val shares: Int = 0,
    val views: Int = 0,
    var visibility: Boolean,
    @Embedded
    var attachment: AttachmentEmbeddable?
) {
    fun toDto() = Post(
        id,
        author,
        content,
        published,
        likedByMe,
        likes,
        shares,
        views,
        authorAvatar,
        attachment?.toDto()
    )

    companion object {
        fun fromDto(dto: Post, visibility: Boolean) =
            PostEntity(
                dto.id,
                dto.author,
                dto.authorAvatar,
                dto.content,
                dto.published,
                dto.likedByMe,
                dto.likes,
                dto.shares,
                dto.views,
                visibility = visibility,
                AttachmentEmbeddable.fromDto(dto.attachment)
            )

    }
}
data class AttachmentEmbeddable(
    var url: String,
    var description: String,
    var type: AttachmentType,
) {
    fun toDto() = Attachment(url, description, type)

    companion object {
        fun fromDto(dto: Attachment?) = dto?.let {
            AttachmentEmbeddable(it.url, it.description, it.type)
        }
    }
}

fun List<PostEntity>.toDto(): List<Post> = map(PostEntity::toDto)
fun List<Post>.toEntity(visibility: Boolean): List<PostEntity> = map {PostEntity.fromDto(it, visibility)}