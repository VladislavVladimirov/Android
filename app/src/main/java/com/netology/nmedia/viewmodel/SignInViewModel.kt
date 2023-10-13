package com.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.netology.nmedia.api.Api
import com.netology.nmedia.auth.AppAuth
import com.netology.nmedia.error.ApiError
import com.netology.nmedia.model.AuthModelState
import kotlinx.coroutines.launch


class SignInViewModel(application: Application) : AndroidViewModel(application) {
    private val _dataState = MutableLiveData<AuthModelState>()
    val dataState: LiveData<AuthModelState>
        get() = _dataState

    fun signIn(login: String, pass: String) = viewModelScope.launch {
        _dataState.value = AuthModelState(loading = true)
        try {
            val response = Api.retrofitService.updateUser(login, pass)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            AppAuth.getInstance().setUser(requireNotNull(response.body()))
            _dataState.value = AuthModelState(success = true)
        } catch (e: Exception) {
            _dataState.value = AuthModelState(error = true)
        }
    }

    fun clean() {
        _dataState.value = AuthModelState(loading = false, error = false, success = false)
    }
}
