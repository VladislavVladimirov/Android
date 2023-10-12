package com.netology.nmedia.dto

import com.netology.nmedia.enums.AttachmentType

data class Attachment(
    val url: String,
    val type: AttachmentType
)
