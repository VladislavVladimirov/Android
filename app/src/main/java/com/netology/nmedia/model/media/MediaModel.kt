package com.netology.nmedia.model.media

import android.net.Uri
import com.netology.nmedia.enums.AttachmentType
import java.io.InputStream

data class MediaModel(
    val uri: Uri? = null,
    val inputStream: InputStream? = null,
    val type: AttachmentType? = null
)