package com.netology.nmedia.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.netology.nmedia.auth.AppAuth
import kotlinx.coroutines.Dispatchers

class AuthViewModel(
    private val appAuth: AppAuth
) : ViewModel() {
    val authLiveData = appAuth.authStateFlow.asLiveData(Dispatchers.Default)
    val isAuthorized: Boolean
        get() = appAuth.authStateFlow.value.token != null
}