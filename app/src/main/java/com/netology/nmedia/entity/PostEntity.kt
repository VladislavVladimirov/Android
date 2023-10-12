package com.netology.nmedia.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.netology.nmedia.enums.AttachmentType
import com.netology.nmedia.dto.Attachment
import com.netology.nmedia.dto.Post

@Entity
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val author: String,
    val authorAvatar: String,
    val authorId: Long,
    val content: String,
    val published: String,
    val likedByMe: Boolean,
    val likes: Int = 0,
    val shares: Int = 0,
    val views: Int = 0,
    var visibility: Boolean,
    @Embedded
    var attachment: AttachmentEmbeddable?,
) {
    fun toDto() = Post(
        id,
        author,
        authorId,
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
                id = dto.id,
                author = dto.author,
                authorAvatar = dto.authorAvatar,
                authorId = dto.authorId,
                content = dto.content,
                published = dto.published,
                likedByMe = dto.likedByMe,
                likes = dto.likes,
                shares = dto.shares,
                views = dto.views,
                visibility = visibility,
                attachment = AttachmentEmbeddable.fromDto(dto.attachment)
            )

    }
}
data class AttachmentEmbeddable(
    var url: String,
    var type: AttachmentType,
) {
    fun toDto() = Attachment(url, type)

    companion object {
        fun fromDto(dto: Attachment?) = dto?.let {
            AttachmentEmbeddable(it.url, it.type)
        }
    }
}

fun List<PostEntity>.toDto(): List<Post> = map(PostEntity::toDto)
fun List<Post>.toEntity(visibility: Boolean): List<PostEntity> = map {PostEntity.fromDto(it, visibility)}