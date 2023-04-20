package com.netology.nmedia.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.netology.MyApp.R
import com.netology.nmedia.dto.Post

class PostRepositorySharedPrefsImpl(
    context: Context,
) : PostRepository {
    private val gson = Gson()
    private val prefs = context.getSharedPreferences("repo", Context.MODE_PRIVATE)
    val type = TypeToken.getParameterized(List::class.java, Post:: class.java).type
    private val key = "posts"
    private var nextId = 1L
    private var posts = emptyList<Post>()
    private val data = MutableLiveData(posts)

    init {
        prefs.getString(key, null)?.let {
            posts = gson.fromJson(it,type)
            data.value = posts
        }
    }

    override fun getAll(): LiveData<List<Post>> = data

    override fun save(post: Post) {
        if (post.id == 0L) {
            posts =  listOf(
                post.copy(
                    id = nextId++,
                    author = "Нетология. Университет интернет-профессий будущего",
                    likedByMe = false,
                    published = "Только что",
                    likes = 0,
                    shares = 0,
                    views = 0,
                    authorAvatar = R.drawable.ic_launcher_foreground
                )
            ) + posts
            data.value = posts
            sync()
            return
        }
        posts = posts.map{
            if (it.id !=post.id) it  else it.copy(content = post.content)
        }
        data.value = posts
        sync()
    }
    override fun likeById(id: Long) {
        posts = posts.map {
            if (it.id != id) it else it.copy(likedByMe = !it.likedByMe)
        }
        posts = posts.map {
            if (it.id != id) it else if (it.likedByMe) {
                it.copy(likedByMe = it.likedByMe, likes = it.likes + 1)
            } else {
                it.copy(likedByMe = it.likedByMe, likes = it.likes - 1)
            }
        }
        data.value = posts
        sync()
    }

    override fun shareById(id: Long) {
        posts = posts.map {
            if (it.id != id) it else it.copy(shares = it.shares + 1)
        }
        data.value = posts
        sync()
    }

    override fun removeById(id: Long) {
        posts = posts.filter { it.id != id}
        data.value = posts
        sync()
    }
    private fun sync() {
        with(prefs.edit()) {
            putString(key, gson.toJson(posts))
            apply()
        }
    }

}