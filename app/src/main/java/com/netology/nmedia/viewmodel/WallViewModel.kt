package com.netology.nmedia.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.netology.nmedia.auth.AppAuth
import com.netology.nmedia.dto.Job
import com.netology.nmedia.dto.Post
import com.netology.nmedia.model.wall.WallModelState
import com.netology.nmedia.repository.draft.post.job.DraftNewJobRepository
import com.netology.nmedia.repository.wall.WallRepository
import com.netology.nmedia.util.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

private val emptyJob = Job(
    id = 0,
    name = "",
    position = "",
    start = "",
    finish = "",
    link = null
)

@HiltViewModel
class WallViewModel @Inject constructor(
    private val repository: WallRepository,
    private val draftRepository: DraftNewJobRepository,
    private val appAuth: AppAuth
) : ViewModel() {
    private val _dataState = MutableLiveData<WallModelState>()
    val dataState: LiveData<WallModelState>
        get() = _dataState

    @ExperimentalCoroutinesApi
    @ExperimentalPagingApi
    fun getPosts(id: Int): Flow<PagingData<Post>> = appAuth.authStateFlow
        .flatMapLatest { (myId, _) ->
            repository.getPosts(id)
                .map { postList ->
                    postList.map {
                        it.copy(
                            ownedByMe = it.authorId == myId
                        )
                    }
                }
        }
        .cachedIn(viewModelScope)

    private val edited = MutableLiveData(emptyJob)
    private val _jobCreated = SingleLiveEvent<Unit>()
    val jobCreated: LiveData<Unit>
        get() = _jobCreated

    fun getJobs() = repository.getJobs()

    fun loadJobsById(id: Int) =
        viewModelScope.launch {
            try {
                _dataState.value = WallModelState(loading = true)
                repository.loadJobsById(id)
                _dataState.value = WallModelState()
            } catch (e: Exception) {
                _dataState.value = WallModelState(error = true)
            }
        }

    fun saveJob() {
        edited.value?.let { job ->
            _jobCreated.value = Unit
            viewModelScope.launch {
                try {
                    _dataState.value = WallModelState(loading = true)
                    repository.saveJob(job)
                    _dataState.value = WallModelState(loading = false)
                } catch (e: Exception) {
                    _dataState.value = WallModelState(error = true)
                }
            }
        }
        clear()
    }

    fun edit(job: Job) {
        edited.value = job
    }
    fun getEditedJob(): Job? {
        return edited.value
    }
    fun changeContent(
        name: String,
        position: String,
        start: String,
        finish: String,
        link: String
    ) {
        if (edited.value?.name == name.trim()
            && edited.value?.position == position.trim()
            && edited.value?.start == start.trim()
            && edited.value?.finish == finish.trim()
            && edited.value?.link == link.trim()
        ) {
            return
        }
        if (link.isBlank()) {
            edited.value = edited.value?.copy(name = name, position = position,start= start, finish = finish, link = null)
        } else {
            edited.value = edited.value?.copy(name = name, position = position,start= start, finish = finish, link = link)
        }
    }

    fun removeJobById(id: Int) = viewModelScope.launch {
        try {
            repository.removeJobById(id)
        } catch (e: Exception) {
            _dataState.value = WallModelState(error = true)
        }
    }

    fun clear() {
        edited.value?.let {
            edited.value = emptyJob
        }
    }

    fun getLatestPostsById(id: Int) =
        viewModelScope.launch {
            try {
                _dataState.value = WallModelState(loading = true)
                repository.getLatestPostsById(id)
                _dataState.value = WallModelState(loading = false)
            } catch (e: Exception) {
                _dataState.value = WallModelState(error = true)
            }
        }
    fun getDraftName(): String {
        return draftRepository.getDraftJobName()
    }
    fun getDraftPosition(): String {
        return draftRepository.getDraftJobPosition()
    }
    fun getDraftStart(): String {
        return draftRepository.getDraftJobStart()
    }
    fun getDraftFinish(): String {
        return draftRepository.getDraftJobFinish()
    }
    fun getDraftLink(): String {
        return draftRepository.getDraftJobLink()
    }
    fun saveDraftName(text: String) {
        draftRepository.saveDraftJobName(text)
    }
    fun saveDraftPosition(text: String) {
        draftRepository.saveDraftJobPosition(text)
    }
    fun saveDraftStart(text: String) {
        draftRepository.saveDraftJobStart(text)
    }
    fun saveDraftFinish(text: String) {
        draftRepository.saveDraftJobFinish(text)
    }
    fun saveDraftLink(text: String) {
        draftRepository.saveDraftJobLink(text)
    }
    fun clearDrafts() {
        draftRepository.clearDrafts()
    }


}

