package com.netology.nmedia.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity
data class PostKeyEntity(
    @PrimaryKey
    val type: KeyType,
    val id: Int,
) {
    enum class KeyType {
        AFTER,
        BEFORE,
    }
}