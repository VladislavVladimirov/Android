package com.netology.nmedia.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.netology.nmedia.auth.AppAuth
import com.netology.nmedia.dto.Attachment
import com.netology.nmedia.dto.Event
import com.netology.nmedia.enums.AttachmentType
import com.netology.nmedia.enums.EventType
import com.netology.nmedia.model.StateModel
import com.netology.nmedia.model.media.PhotoModel
import com.netology.nmedia.repository.draft.event.DraftNewEventRepository
import com.netology.nmedia.repository.event.EventRepository
import com.netology.nmedia.util.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

private val empty = Event(
    id = 0,
    author = "",
    authorId = 0,
    content = "",
    datetime = "",
    published = "",
    type = EventType.ONLINE
)

@ExperimentalCoroutinesApi
@HiltViewModel
class EventViewModel @Inject constructor(
    private val repository: EventRepository,
    private val draftRepository: DraftNewEventRepository,
    appAuth: AppAuth
) : ViewModel() {
    private val cached = repository
        .data
        .cachedIn(viewModelScope)
    private val _dataState = MutableLiveData<StateModel>()
    val dataState: LiveData<StateModel>
        get() = _dataState
    val data: Flow<PagingData<Event>> =
        appAuth.authStateFlow
            .flatMapLatest { (myId, _) ->
                cached.map { pagingData ->
                    pagingData.map { event ->
                        event.copy(
                            ownedByMe = event.authorId == myId,
                            likedByMe = event.likeOwnerIds.contains(myId),
                            participatedByMe = event.participantsIds.contains(myId)
                        )
                    }
                }
            }
    private val edited = MutableLiveData(empty)
    private val _eventCreated = SingleLiveEvent<Unit>()
    val eventCreated: LiveData<Unit>
        get() = _eventCreated
    private val _photoState = MutableLiveData<PhotoModel?>()
    val photoState: LiveData<PhotoModel?>
        get() = _photoState

    fun changePhoto(photoModel: PhotoModel?) {
        _photoState.value = photoModel
    }

    fun save() {
        edited.value?.let { event ->
            _eventCreated.value = Unit
            viewModelScope.launch {
                try {
                    _photoState.value?.let { photoModel ->
                        photoModel.file?.let { repository.saveWithAttachment(it, event) }
                    } ?: repository.save(event)
                    _dataState.value = StateModel()
                } catch (e: Exception) {
                    _dataState.value = StateModel(error = true)
                }
            }
        }
        _photoState.value = null
        clear()
    }

    fun edit(event: Event) {
        edited.value = event
    }

    fun clear() {
        edited.value?.let {
            edited.value = empty
        }
    }

    fun changeContent(content: String, link: String, date: String, isOnline: Boolean) {
        val text = content.trim()
        val linkText = link.trim()
        val dateText = date.trim()
        var type: EventType = if (isOnline) {
            EventType.ONLINE
        } else {
            EventType.OFFLINE
        }

        if (edited.value?.content == text
            && edited.value?.link == linkText
            && edited.value?.datetime == dateText
            && edited.value?.type == type
        ) {
            return
        }
        if (link.isBlank()) {
            edited.value =
                edited.value?.copy(content = text, link = null, datetime = date, type = type)
        } else {
            edited.value =
                edited.value?.copy(content = text, link = linkText, datetime = date, type = type)
        }
    }

    fun changeAttachmentPhoto(url: String) {
        if (edited.value?.attachment?.url == url.trim()) {
            return
        }
        if (url.isBlank()) {
            edited.value = edited.value?.copy(attachment = null)
        }
        edited.value =
            edited.value?.copy(attachment = Attachment(url.trim(), type = AttachmentType.IMAGE))
    }

    fun likeById(event: Event) = viewModelScope.launch {
        try {
            if (!event.likedByMe) {
                repository.likeById(event.id)
            } else {
                repository.dislikeById(event.id)
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

    fun takePartById(event: Event) = viewModelScope.launch {
        try {
            if (!event.participatedByMe) {
                repository.takePartAtEvent(event.id)
            } else {
                repository.deleteTakingPart(event.id)
            }
        } catch (e: Exception) {
            _dataState.value = StateModel(error = true)
        }
    }

    fun getEditedEvent(): Event? {
        return edited.value
    }

    fun getDraftContent(): String {
        return draftRepository.getDraftContent()
    }

    fun getDraftLink(): String {
        return draftRepository.getDraftLink()
    }

    fun getDraftDate(): String {
        return draftRepository.getDraftDate()
    }

    fun getDraftTime(): String {
        return draftRepository.getDraftTime()
    }

    fun getDraftFormat(): String {
        return draftRepository.getDraftFormat()
    }

    fun saveDraftContent(text: String) {
        draftRepository.saveDraftContent(text)
    }

    fun saveDraftLink(text: String) {
        draftRepository.saveDraftLink(text)
    }

    fun saveDraftDate(text: String) {
        draftRepository.saveDraftDate(text)
    }

    fun saveDraftTime(text: String) {
        draftRepository.saveDraftTime(text)
    }

    fun saveDraftFormat(text: String) {
        draftRepository.saveDraftFormat(text)
    }

    fun clearDrafts() {
        draftRepository.clearDrafts()
    }
}