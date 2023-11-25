package com.netology.nmedia.model.media

import android.net.Uri
import java.io.File

data class PhotoModel(
    val uri: Uri? = null,
    val file: File? = null
)