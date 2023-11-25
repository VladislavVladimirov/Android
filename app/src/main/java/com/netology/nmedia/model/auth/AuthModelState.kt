package com.netology.nmedia.model.auth

data class AuthModelState(
    val loading: Boolean = false,
    val error: Boolean = false,
    val success: Boolean = false
)
