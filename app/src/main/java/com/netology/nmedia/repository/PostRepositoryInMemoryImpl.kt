package com.netology.nmedia.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.netology.MyApp.R
import com.netology.nmedia.dto.Post


class PostRepositoryInMemoryImpl : PostRepository {
    private var post = Post(
        id = 1,
        author = "Нетология. Университет интернет-профессий будущего",
        content = "Привет, это новая Нетология! Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и упралению. Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов. Но самое важное остаётся с нами: мы верим, что в каждом уже есть сила, которая заставляет хотеть больше, целиться выше, бежать быстрее. Наша миссия — помочь встать на путь роста и начать цепочку перемен → http://netolo.gy//fyb ",
        published = "2 сентября в 18:30",
        likedByMe = false,
        likes = 12,
        shares = 14,
        views = 4543,
        authorAvatar = R.drawable.ic_launcher_foreground
    )
    private val data = MutableLiveData(post)

    override fun get(): LiveData<Post> = data
    override fun like() {

        post = post.copy(likedByMe = !post.likedByMe)
        post = if (post.likedByMe) {
            post.copy(likedByMe = post.likedByMe, likes = post.likes + 1)
        } else {
            post.copy(likedByMe = post.likedByMe, likes = post.likes - 1)
        }
        data.value = post
    }
    override fun share() {
        post = post.copy(shares = post.shares + 1)
        data.value = post
    }

}