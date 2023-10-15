package com.netology.nmedia.viewmodel


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.netology.nmedia.auth.AppAuth
import com.netology.nmedia.dto.Post
import com.netology.nmedia.model.FeedModel
import com.netology.nmedia.model.FeedModelState
import com.netology.nmedia.model.PhotoModel
import com.netology.nmedia.repository.DraftRepository
import com.netology.nmedia.repository.PostRepository
import com.netology.nmedia.util.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch


private val empty = Post(
    id = 0L,
    author = "",
    authorId = 0,
    content = "",
    published = "",
    likedByMe = false,
    likes = 0,
    shares = 0,
    views = 0,
    authorAvatar = "",
)

class PostViewModel(
    private val repository: PostRepository,
    private val draftRepository: DraftRepository,
    appAuth: AppAuth
) : ViewModel() {

    private val _dataState = MutableLiveData<FeedModelState>()
    val dataState: LiveData<FeedModelState>
        get() = _dataState
    @OptIn(ExperimentalCoroutinesApi::class)
    val data: LiveData<FeedModel> = appAuth
        .authStateFlow
        .flatMapLatest { (myId, _) ->
            repository.data
                .map { posts ->
                    FeedModel(
                        posts.map { it.copy(ownedByMe = it.authorId == myId) },
                        posts.isEmpty()
                    )
                }
        }.asLiveData(Dispatchers.Default)
    private val edited = MutableLiveData(empty)
    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated
    val newer: LiveData<Int> = data.switchMap {
        repository.getNewerCount(it.posts.firstOrNull { post ->
            post.author != "Student"
        }?.id ?: 0L)
            .asLiveData(Dispatchers.Default)
    }
    private val _photoState = MutableLiveData<PhotoModel?>()
    val photoState: LiveData<PhotoModel?>
        get() = _photoState

    init {
        loadPosts()
    }

    fun changePhoto(photoModel: PhotoModel?) {
        _photoState.value = photoModel
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
        edited.value?.let { post ->
            _postCreated.value = Unit
            viewModelScope.launch {
                try {
                    _photoState.value?.let { photoModel ->
                        photoModel.file?.let { repository.saveWithAttachment(it, post) }
                    } ?: repository.save(post)
                    _dataState.value = FeedModelState()
                } catch (e: Exception) {
                    _dataState.value = FeedModelState(error = true)
                }
            }
        }
        _photoState.value = null
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
            if (!post.likedByMe) {
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

    fun showHiddenPosts() = viewModelScope.launch {
        try {
            _dataState.value = FeedModelState(loading = true)
            repository.showAll()
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }

    fun removeById(id: Long) = viewModelScope.launch {
        try {
            repository.removeById(id)
        } catch (e: Exception) {
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
