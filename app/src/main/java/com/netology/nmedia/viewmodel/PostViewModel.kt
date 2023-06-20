package com.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.netology.nmedia.dto.Post
import com.netology.nmedia.model.FeedModel
import com.netology.nmedia.repository.DraftRepository
import com.netology.nmedia.repository.DraftRepositorySharedPrefsImpl
import com.netology.nmedia.repository.PostRepository
import com.netology.nmedia.repository.PostRepositoryImpl
import com.netology.nmedia.util.SingleLiveEvent


private val empty = Post(
    id = 0,
    author = "",
    content = "",
    published = 0,
    likedByMe = false,
    likes = 0,
    shares = 0,
    views = 0,
    authorAvatar = null,
    attachment = null
)

class PostViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PostRepository = PostRepositoryImpl()
    private val draftRepository: DraftRepository = DraftRepositorySharedPrefsImpl(application)
    private val _data = MutableLiveData(FeedModel())
    val data: LiveData<FeedModel>
        get() = _data
    private val edited = MutableLiveData(empty)
    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    init {
        loadPosts()
    }

    fun loadPosts() {
        _data.postValue(_data.value?.copy(loading = true)) // Здесь старые посты оставляем
        repository.getAll(object : PostRepository.PostsCallback<List<Post>> {
            override fun onSuccess(input: List<Post>) {
                _data.postValue(FeedModel(posts = input, empty = input.isEmpty()))
            }

            override fun onError(e: Exception) {
                _data.postValue(FeedModel(error = true))
            }
        })
    }

    fun save() {
        edited.value?.let {

            repository.save(it, object : PostRepository.PostsCallback<Post> {
                override fun onSuccess(input: Post) {
                    _postCreated.postValue(Unit)
                }

                override fun onError(e: Exception) {
                    _data.postValue(FeedModel(error = true))
                }

            })
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

    fun likeById(post: Post) {
        if (!post.likedByMe){
            repository.likeById(post.id, object : PostRepository.PostsCallback<Post> {

                override fun onSuccess(input: Post) {
                    _data.postValue(FeedModel(posts = _data.value?.posts.orEmpty().map {
                        if (it.id == post.id) input else it

                    }))
                    loadPosts()
                }

                override fun onError(e: Exception) {
                    _data.postValue(FeedModel(error = true))
                }
            })
        } else {
            repository.dislikeById(post.id,object : PostRepository.PostsCallback<Post> {

                override fun onSuccess(input: Post) {
                    _data.postValue(FeedModel(posts = _data.value?.posts.orEmpty().map {
                        if (it.id == post.id) input else it

                    }))
                    loadPosts()
                }

                override fun onError(e: Exception) {
                    _data.postValue(FeedModel(error = true))
                }
            })
        }

    }

    fun removeById(id: Long) {
        val old = _data.value?.posts.orEmpty()
        repository.removeById(id, object : PostRepository.PostsCallback<Unit> {
            override fun onSuccess(input: Unit) {
                _data.postValue(
                    _data.value?.copy(
                        posts = _data.value?.posts.orEmpty()
                            .filter { it.id != id }
                    ))
            }

            override fun onError(e: Exception) {
                _data.postValue(_data.value?.copy(posts = old))
            }
        })
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
