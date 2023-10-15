package com.netology.nmedia.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.netology.nmedia.api.ApiService
import com.netology.nmedia.auth.AppAuth
import com.netology.nmedia.repository.DraftRepository
import com.netology.nmedia.repository.PostRepository


class ViewModelFactory(
    private val repository: PostRepository,
    private val draftRepository: DraftRepository,
    private val appAuth: AppAuth,
    private val apiService: ApiService
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
    when {
        modelClass.isAssignableFrom(PostViewModel::class.java) -> {
            PostViewModel( repository, draftRepository , appAuth) as T
        }
        modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
            AuthViewModel(appAuth) as T
        }
        modelClass.isAssignableFrom(SignInViewModel::class.java) -> {
            SignInViewModel(appAuth, apiService) as T
        }
        modelClass.isAssignableFrom(SignUpViewModel::class.java) -> {
            SignUpViewModel(appAuth,apiService) as T
        }
        else -> error("Unknown class: $modelClass")
    }
}