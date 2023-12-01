package com.netology.nmedia.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.netology.nmedia.dto.Event
import com.netology.nmedia.enums.EventType
import com.netology.nmedia.util.Converters

@Entity
data class EventEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val author: String,
    val authorAvatar: String?,
    val authorId: Int,
    val authorJob: String?,
    val content: String,
    val datetime: String,
    @TypeConverters(Converters::class)
    val likeOwnerIds: List<Int>,
    val likedByMe: Boolean,
    val link: String?,
    val ownedByMe: Boolean,
    @TypeConverters(Converters::class)
    val participantsIds: List<Int>,
    val participatedByMe: Boolean,
    val published: String,
    @TypeConverters(Converters::class)
    val speakerIds: List<Int>,
    val type: EventType,
    @Embedded
    val attachment: AttachmentEmbeddable?
) {
    fun toDto() = Event(
        id,
        author,
        authorAvatar,
        authorId,
        authorJob,
        content,
        datetime,
        likeOwnerIds,
        likedByMe,
        link,
        ownedByMe,
        participantsIds,
        participatedByMe,
        published,
        speakerIds,
        type,
        attachment?.toDto()
    )

    companion object {
        fun fromDto(dto: Event) =
            EventEntity(
                dto.id,
                dto.author,
                dto.authorAvatar,
                dto.authorId,
                dto.authorJob,
                dto.content,
                dto.datetime,
                dto.likeOwnerIds,
                dto.likedByMe,
                dto.link,
                dto.ownedByMe,
                dto.participantsIds,
                dto.participatedByMe,
                dto.published,
                dto.speakerIds,
                dto.type,
                AttachmentEmbeddable.fromDto(dto.attachment),
                )
    }
}
fun List<EventEntity>.toDto(): List<Event> = map(EventEntity::toDto)
fun List<Event>.toEventEntity(): List<EventEntity> =
    map {
        EventEntity.fromDto(it)
    }
