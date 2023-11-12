package com.netology.nmedia.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.netology.nmedia.model.AuthModelState
import com.netology.nmedia.model.PhotoModel
import com.netology.nmedia.repository.auth.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject



@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {
    private val _dataState = MutableLiveData<AuthModelState>()
    val dataState: LiveData<AuthModelState>
        get() = _dataState
    private val _photoState = MutableLiveData<PhotoModel?>()
    val photoState: LiveData<PhotoModel?>
        get() = _photoState


    fun signUp(login: String, pass: String, name: String) = viewModelScope.launch {
        try {
            _dataState.value = AuthModelState(loading = true)
            if (_photoState.value == null) {
                repository.signUp(login, pass, name)
            } else {
                _photoState.value?.file?.let { file ->
                    repository.signUpWithAvatar(login, pass, name, file)
                }
            }
            _dataState.value = AuthModelState(success = true)
        } catch (e: Exception) {
            _dataState.value = AuthModelState(error = true)
        }
    }

    fun clean() {
        _dataState.value = AuthModelState(loading = false, error = false, success = false)
    }

    fun changePhoto(photoModel: PhotoModel?) {
        _photoState.value = photoModel
    }
}