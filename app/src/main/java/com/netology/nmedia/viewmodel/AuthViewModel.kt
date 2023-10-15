package com.netology.nmedia.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.netology.nmedia.auth.AppAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val appAuth: AppAuth
) : ViewModel() {
    val authLiveData = appAuth.authStateFlow.asLiveData(Dispatchers.Default)
    val isAuthorized: Boolean
        get() = appAuth.authStateFlow.value.token != null
}