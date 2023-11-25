package com.netology.nmedia.model.wall

data class WallModelState(
    val loading: Boolean = false,
    val error: Boolean = false,
    val refreshing: Boolean = false,
)
