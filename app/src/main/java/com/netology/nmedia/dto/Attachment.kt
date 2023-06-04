package com.netology.nmedia.dto

import com.netology.nmedia.activity.enums.AttachmentType

data class Attachment(
    val url: String,
    val description: String,
    val type: AttachmentType
)
