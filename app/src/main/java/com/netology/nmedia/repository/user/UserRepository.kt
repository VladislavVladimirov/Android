package com.netology.nmedia.repository.user

import com.netology.nmedia.dto.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    val data: Flow<List<User>>

    suspend fun getAll()
    suspend fun getUserById(id: Int): User
}