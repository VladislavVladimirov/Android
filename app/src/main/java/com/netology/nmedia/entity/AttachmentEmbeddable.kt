package com.netology.nmedia.entity

import com.netology.nmedia.dto.Attachment
import com.netology.nmedia.enums.AttachmentType

data class AttachmentEmbeddable(
    var url: String,
    var attachmentType: AttachmentType,
) {
    fun toDto() = Attachment(url, attachmentType)

    companion object {
        fun fromDto(dto: Attachment?) = dto?.let {
            AttachmentEmbeddable(it.url, it.type)
        }
    }
}