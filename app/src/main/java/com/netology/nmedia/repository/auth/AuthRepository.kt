package com.netology.nmedia.repository.auth

import java.io.File

interface AuthRepository {
    suspend fun signIn(login: String, pass: String)
    suspend fun signUp(login: String, pass: String, name:String)
    suspend fun signUpWithAvatar(login: String, pass: String, name:String, file: File)
}