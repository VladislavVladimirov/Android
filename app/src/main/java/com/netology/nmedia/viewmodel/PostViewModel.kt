package com.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.netology.nmedia.db.AppDb
import com.netology.nmedia.dto.Post
import com.netology.nmedia.model.FeedModel
import com.netology.nmedia.model.FeedModelState
import com.netology.nmedia.repository.DraftRepository
import com.netology.nmedia.repository.DraftRepositorySharedPrefsImpl
import com.netology.nmedia.repository.PostRepository
import com.netology.nmedia.repository.PostRepositoryImpl
import com.netology.nmedia.util.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch


private val empty = Post(
    id = 0L,
    author = "",
    content = "",
    published = "",
    likedByMe = false,
    likes = 0,
    shares = 0,
    views = 0,
    authorAvatar = "",
)

class PostViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PostRepository = PostRepositoryImpl(AppDb.getInstance(context = application).postDao())
    private val draftRepository: DraftRepository = DraftRepositorySharedPrefsImpl(application)
    private val _dataState = MutableLiveData<FeedModelState>()
    val dataState: LiveData<FeedModelState>
        get() = _dataState
    val data: LiveData<FeedModel> = repository.data.map(::FeedModel).asLiveData(Dispatchers.Default)
    private val edited = MutableLiveData(empty)
    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated
    val newer: LiveData<Int> = data.switchMap {
        repository.getNewerCount(it.posts.firstOrNull()?.id ?: 0L)
            .asLiveData(Dispatchers.Default)
    }
    init {
        loadPosts()
    }

    fun loadPosts() = viewModelScope.launch {
        try {
            _dataState.value = FeedModelState(loading = true)
            repository.getAll()
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }
    fun refreshPosts() = viewModelScope.launch {
        try {
            _dataState.value = FeedModelState(refreshing = true)
            repository.getAll()
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }

    fun save() {
        edited.value?.let {
            _postCreated.value = Unit
            viewModelScope.launch {
                try {
                    repository.save(it)
                    _dataState.value = FeedModelState()
                } catch (e: Exception) {
                    _dataState.value = FeedModelState(error = true)
                }
            }
        }
        edited.value = empty
    }

    fun edit(post: Post) {
        edited.value = post
    }

    fun changeContent(content: String) {
        val text = content.trim()
        if (edited.value?.content == text) {
            return
        }
        edited.value = edited.value?.copy(content = text)
    }

    fun cancelEdit() {
        edited.value?.let {
            edited.value = empty
        }
    }

    fun likeById(post: Post) = viewModelScope.launch {
        try {
            if (!post.likedByMe){
                repository.likeById(post.id)
                loadPosts()
            } else {
                repository.dislikeById(post.id)
                loadPosts()
            }
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }

    fun removeById(id: Long) = viewModelScope.launch {
        try {
            repository.removeById(id)
        } catch (e:Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }

    fun getDraft(): String {
        return draftRepository.getDraft()
    }

    fun saveDraft(text: String) {
        draftRepository.saveDraft(text)
    }

    fun clearDraft() {
        draftRepository.clearDraft()
    }


}
