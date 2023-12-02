package com.netology.nmedia.viewmodel


import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.netology.nmedia.auth.AppAuth
import com.netology.nmedia.dto.Attachment
import com.netology.nmedia.dto.Post
import com.netology.nmedia.enums.AttachmentType
import com.netology.nmedia.model.StateModel
import com.netology.nmedia.model.media.MediaModel
import com.netology.nmedia.repository.draft.post.DraftNewPostRepository
import com.netology.nmedia.repository.post.PostRepository
import com.netology.nmedia.util.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.io.InputStream
import javax.inject.Inject


private val empty = Post(
    id = 0,
    authorId = 0,
    author = "",
    content = "",
    published = "",
)

@ExperimentalCoroutinesApi
@HiltViewModel
class PostViewModel @Inject constructor(
    private val repository: PostRepository,
    private val draftRepository: DraftNewPostRepository,
    appAuth: AppAuth
) : ViewModel() {
    private val cached = repository
        .data
        .cachedIn(viewModelScope)
    private val _dataState = MutableLiveData<StateModel>()
    val dataState: LiveData<StateModel>
        get() = _dataState

    val data: Flow<PagingData<Post>> =
        appAuth.authStateFlow
            .flatMapLatest { (myId, _) ->
                cached.map { pagingData ->
                    pagingData.map { post ->
                        post.copy(
                            ownedByMe = post.authorId == myId,
                            likedByMe = post.likeOwnerIds.contains(myId)
                        )
                    }
                }
            }
    private val edited = MutableLiveData(empty)
    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    private val _mediaState = MutableLiveData<MediaModel?>()
    val mediaState: LiveData<MediaModel?>
        get() = _mediaState

    fun changeMedia(uri: Uri?, inputStream: InputStream?, type: AttachmentType?) {
        _mediaState.value = MediaModel(uri, inputStream, type)
    }
    fun save() {
        edited.value?.let { post ->
            _postCreated.value = Unit
            viewModelScope.launch {
                try {
                    _mediaState.value?.let { media ->
                        media.inputStream?.let { repository.saveWithAttachment(it, media.type!!, post) }
                    } ?: repository.save(post)
                    _dataState.value = StateModel()
                } catch (e: Exception) {
                    _dataState.value = StateModel(error = true)
                }
            }
        }
        _mediaState.value = null
        clear()
    }

    fun edit(post: Post) {
        edited.value = post
    }

    fun changeContent(content: String, link: String) {
        val text = content.trim()
        val linkText = link.trim()
        if (edited.value?.content == text && edited.value?.link == linkText) {
            return
        }
        if (link.isBlank()) {
            edited.value = edited.value?.copy(content = text, link = null)
        } else {
            edited.value = edited.value?.copy(content = text, link = linkText)
        }
    }

    fun changeAttachmentPhoto(url: String) {
        if (edited.value?.attachment?.url == url.trim()) {
            return
        }
        if (url == "") {
            edited.value = edited.value?.copy(attachment = null)
        } else {
            edited.value = edited.value?.copy(attachment = Attachment(url.trim(), type = AttachmentType.IMAGE))
        }
    }
    fun changeAttachmentAudio(url: String) {
        if (edited.value?.attachment?.url == url.trim()) {
            return
        }
        if (url == "") {
            edited.value = edited.value?.copy(attachment = null)
        } else {
            edited.value = edited.value?.copy(attachment = Attachment(url.trim(), type = AttachmentType.AUDIO))
        }
    }
    fun changeAttachmentVideo(url: String) {
        if (edited.value?.attachment?.url == url.trim()) {
            return
        }
        if (url == "") {
            edited.value = edited.value?.copy(attachment = null)
        } else {
            edited.value = edited.value?.copy(attachment = Attachment(url.trim(), type = AttachmentType.VIDEO))
        }
    }

    fun clear() {
        edited.value?.let {
            edited.value = empty
        }
    }

    fun likeById(post: Post) = viewModelScope.launch {
        try {
            if (!post.likedByMe) {
                repository.likeById(post.id)
            } else {
                repository.dislikeById(post.id)
            }
        } catch (e: Exception) {
            _dataState.value = StateModel(error = true)
        }
    }

    fun removeById(id: Int) = viewModelScope.launch {
        try {
            repository.removeById(id)
        } catch (e: Exception) {
            _dataState.value = StateModel(error = true)
        }
    }

    fun getEditedPost(): Post? {
        return edited.value
    }

    fun getDraftContent(): String {
        return draftRepository.getDraftContent()
    }

    fun getDraftLink(): String {
        return draftRepository.getDraftLink()
    }

    fun saveDraftContent(text: String) {
        draftRepository.saveDraftContent(text)
    }

    fun saveDraftLink(text: String) {
        draftRepository.saveDraftLink(text)
    }

    fun clearDrafts() {
        draftRepository.clearDrafts()
    }

}
