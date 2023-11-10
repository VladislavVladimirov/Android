package com.netology.nmedia.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.netology.nmedia.model.AuthModelState
import com.netology.nmedia.repository.auth.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
   private val repository: AuthRepository
) : ViewModel() {
    private val _dataState = MutableLiveData<AuthModelState>()
    val dataState: LiveData<AuthModelState>
        get() = _dataState

    fun signIn(login: String, pass: String) = viewModelScope.launch {
        _dataState.value = AuthModelState(loading = true)
        try {
            repository.signIn(login, pass)
            _dataState.value = AuthModelState(success = true)
        } catch (e: Exception) {
            _dataState.value = AuthModelState(error = true)
        }
    }

    fun clean() {
        _dataState.value = AuthModelState(loading = false, error = false, success = false)
    }
}
