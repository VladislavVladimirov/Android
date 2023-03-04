package com.netology.nmedia

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.netology.MyApp.R
import com.netology.MyApp.databinding.ActivityMainBinding
import com.netology.nmedia.PostService.formatCount
import com.netology.nmedia.dto.Post

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val post = Post(
            id = 1,
            author = "Нетология. Университет интернет-профессий будущего",
            content = "Привет, это новая Нетология! Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и упралению. Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов. Но самое важное остаётся с нами: мы верим, что в каждом уже есть сила, которая заставляет хотеть больше, целиться выше, бежать быстрее. Наша миссия — помочь встать на путь роста и начать цепочку перемен → http://netolo.gy//fyb ",
            published = "2 сентября в 18:30",
            likedByMe = false,
            likes = 1245545,
            reposts = 10,
            views = 242,
            authorAvatar = R.drawable.ic_launcher_foreground
        )
        with(binding) {
            author.text = post.author
            published.text = post.published
            content.text = post.content
            avatar.setImageResource(post.authorAvatar)
            if (post.likedByMe) {
                like?.setImageResource(R.drawable.ic_liked_24)
            }
            likesCount?.text = formatCount(post.likes)
            repostCount?.text = post.reposts.toString()
            viewsCount?.text = post.views.toString()
            like?.setOnClickListener {
                post.likedByMe = !post.likedByMe
                if (post.likedByMe) post.likes++ else post.likes--
                like.setImageResource(
                    if (post.likedByMe) R.drawable.ic_liked_24 else R.drawable.ic_like_24
                )
                likesCount?.text = formatCount(post.likes)
            }
            repost?.setOnClickListener {
                post.reposts++
                repostCount?.text = post.reposts.toString()
            }
        }

    }

}