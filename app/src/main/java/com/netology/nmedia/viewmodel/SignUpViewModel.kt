package com.netology.nmedia.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.netology.nmedia.auth.AppAuth
import com.netology.nmedia.enums.AttachmentType
import com.netology.nmedia.model.auth.AuthModelState
import com.netology.nmedia.model.media.MediaModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.InputStream
import javax.inject.Inject


@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val appAuth: AppAuth
) : ViewModel() {
    private val _dataState = MutableLiveData<AuthModelState>()
    val dataState: LiveData<AuthModelState>
        get() = _dataState
    private val _photoState = MutableLiveData<MediaModel>()
    val photoState: LiveData<MediaModel>
        get() = _photoState
    val noAvatar = MediaModel(null, null, null)

    fun signUp(login: String, pass: String, name: String) = viewModelScope.launch {
        try {
            _dataState.value = AuthModelState(loading = true)
            when (_photoState.value) {
                noAvatar -> appAuth.signUp(login, pass, name)
                else -> _photoState.value?.inputStream?.let { media ->
                    appAuth.signUpWithAvatar(
                        login,
                        pass,
                        name,
                        media
                    )
                }
            }
            _dataState.value = AuthModelState(success = true)
        } catch (e: Exception) {
            _dataState.value = AuthModelState(error = true)
            e.printStackTrace()
        }
    }

    fun clean() {
        _dataState.value = AuthModelState(loading = false, error = false, success = false)
    }

    fun changeMedia(uri: Uri?, inputStream: InputStream?, type: AttachmentType?) {
        _photoState.value = MediaModel(uri, inputStream, type)
    }
}