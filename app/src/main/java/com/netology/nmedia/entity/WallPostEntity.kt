package com.netology.nmedia.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.netology.nmedia.dto.Coordinates
import com.netology.nmedia.dto.Post
import com.netology.nmedia.util.Converters

@Entity
data class WallPostEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val authorId: Int,
    val author: String,
    val authorAvatar: String?,
    val authorJob: String?,
    val content: String,
    val published: String,
    val coords: String,
    val link: String?,
    @TypeConverters(Converters::class)
    val likeOwnerIds: List<Int>,
    @TypeConverters(Converters::class)
    val mentionIds: List<Int>,
    val mentionedMe: Boolean,
    val likedByMe: Boolean,
    val ownedByMe: Boolean,
    @Embedded
    var attachment: AttachmentEmbeddable?
) {
    fun toDto() = Post(
        id,
        authorId,
        author,
        authorAvatar,
        authorJob,
        content,
        published,
        coords = if (coords.isBlank()) null else Coordinates(
            coords.split("/").first(),
            coords.split("/").last()
        ),
        link,
        likeOwnerIds,
        mentionIds,
        mentionedMe,
        likedByMe,
        attachment?.toDto(),
        ownedByMe,
    )
    companion object {
        fun fromDto(dto: Post) =
            WallPostEntity(
                dto.id,
                dto.authorId,
                dto.author,
                dto.authorAvatar,
                dto.authorJob,
                dto.content,
                dto.published,
                coords = if (dto.coords != null)
                    "${dto.coords.lat}/${dto.coords.long}"
                else
                    "",
                dto.link,
                dto.likeOwnerIds,
                dto.mentionIds,
                dto.mentionedMe,
                dto.likedByMe,
                dto.ownedByMe,
                AttachmentEmbeddable.fromDto(dto.attachment),
            )

    }
}
fun List<WallPostEntity>.toDto(): List<Post> = map(WallPostEntity::toDto)
fun List<Post>.toWallPostEntity(): List<WallPostEntity> = map { WallPostEntity.fromDto(it) }