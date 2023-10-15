package com.netology.nmedia.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.netology.nmedia.api.ApiService
import com.netology.nmedia.auth.AppAuth
import com.netology.nmedia.error.ApiError
import com.netology.nmedia.model.AuthModelState
import com.netology.nmedia.model.PhotoModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject
@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val appAuth: AppAuth,
    private val apiService: ApiService
) : ViewModel() {
    private val _dataState = MutableLiveData<AuthModelState>()
    val dataState: LiveData<AuthModelState>
        get() = _dataState
    private val _photoState = MutableLiveData<PhotoModel?>()
    val photoState: LiveData<PhotoModel?>
        get() = _photoState

    fun signUpWithAvatar(login: String, pass: String, name: String, file: File) =
        viewModelScope.launch {
            val part = MultipartBody.Part.createFormData(
                "file.png",
                file.name,
                file.asRequestBody()
            )
            _dataState.value = AuthModelState(loading = true)
            try {
                val response = apiService.registerWithPhoto(
                    login.toRequestBody("text/plain".toMediaType()),
                    pass.toRequestBody("text/plain".toMediaType()),
                    name.toRequestBody("text/plain".toMediaType()),
                    part
                )
                if (!response.isSuccessful) {
                    throw ApiError(response.code(), response.message())
                }
              appAuth.setUser(requireNotNull(response.body()))
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