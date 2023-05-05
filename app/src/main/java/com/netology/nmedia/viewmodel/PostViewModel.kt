package com.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.netology.nmedia.db.AppDb
import com.netology.nmedia.dto.Post
import com.netology.nmedia.repository.DraftRepository
import com.netology.nmedia.repository.DraftRepositorySharedPrefsImpl
import com.netology.nmedia.repository.PostRepository
import com.netology.nmedia.repository.PostRepositorySQLiteImpl

private val empty = Post(
    id = 0,
    author = "",
    content = "",
    published = "",
    likedByMe = false,
    likes = 0,
    shares = 0,
    views = 0,
    authorAvatar = 0
)

class PostViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PostRepository = PostRepositorySQLiteImpl(
        AppDb.getInstance(application).postDao
    )
    private val draftRepository: DraftRepository = DraftRepositorySharedPrefsImpl(application)
    val data = repository.getAll()
    private val edited = MutableLiveData(empty)
    fun save() {
        edited.value?.let {
            repository.save(it)
        }
        edited.value = empty
    }
    fun edit(post: Post) {
        edited.value = post

    }

    fun changeContent(content: String) {
        edited.value?.let {
            val text = content.trim()
            if (it.content == text) {
                return
            }
            edited.value = it.copy(content = text)
        }
    }
    fun cancelEdit() {
        edited.value?.let {
            edited.value = empty
        }
    }

    fun likeById(id: Long) = repository.likeById(id)
    fun shareById(id: Long) = repository.shareById(id)
    fun removeById(id: Long) = repository.removeById(id)
    fun viewPostById(id: Long) = repository.viewPostById(id)
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
