package com.netology.nmedia.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.netology.nmedia.dto.User


@Entity
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val login: String,
    val name: String,
    val avatar: String?,
) {
    fun toDto() = User(
        id,
        login,
        name,
        avatar
    )
    companion object {
        fun fromDto(dto:User) = UserEntity(
            dto.id,
            dto.login,
            dto.name,
            dto.avatar
        )
    }
}
fun List<UserEntity>.toDto(): List<User> = map(UserEntity::toDto)
fun List<User>.toUserEntity(): List<UserEntity> =
    map { UserEntity.fromDto(it) }