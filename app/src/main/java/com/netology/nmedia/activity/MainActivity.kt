package com.netology.nmedia.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.netology.MyApp.R
import com.netology.MyApp.databinding.ActivityMainBinding
import com.netology.nmedia.viewmodel.PostViewModel
import androidx.activity.viewModels


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        val viewModel: PostViewModel by viewModels()
        setContentView(binding.root)

        viewModel.data.observe(this) { post ->
            with(binding) {
                author.text = post.author
                published.text = post.published
                content.text = post.content
                avatar.setImageResource(post.authorAvatar)
                if (post.likedByMe) {
                    like.setImageResource(R.drawable.ic_liked_24)
                }
                likesCount.text = viewModel.formatCount(post.likes)
                repostCount.text = viewModel.formatCount(post.shares)
                viewsCount.text = viewModel.formatCount(post.views)
                like.setOnClickListener {
                    viewModel.like()
                    like.setImageResource(
                        if (post.likedByMe) R.drawable.ic_liked_24 else R.drawable.ic_like_24
                    )
                    likesCount.text = viewModel.formatCount(post.likes)
                }
                repost.setOnClickListener {
                    viewModel.share()
                    repostCount.text = viewModel.formatCount(post.shares)
                }
            }

        }

    }
}