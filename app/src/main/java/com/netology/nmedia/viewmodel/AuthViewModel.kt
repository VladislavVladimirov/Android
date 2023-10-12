package com.netology.nmedia.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.netology.nmedia.auth.AppAuth
import kotlinx.coroutines.Dispatchers

class AuthViewModel: ViewModel() {
    val authLiveData = AppAuth.getInstance().authStateFlow.asLiveData(Dispatchers.Default)
    val isAuthorized: Boolean
        get() = AppAuth.getInstance().authStateFlow.value.token != null
}