package com.netology.nmedia.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.netology.MyApp.R
import com.netology.MyApp.databinding.ActivityMainBinding
import com.netology.nmedia.viewmodel.PostViewModel
import androidx.activity.viewModels
import com.netology.nmedia.viewmodel.PostFormatter.formatCount


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
                likesCount.text = formatCount(post.likes)
                repostCount.text = formatCount(post.shares)
                viewsCount.text = formatCount(post.views)
                like.setOnClickListener {
                    viewModel.like()
                }
                repost.setOnClickListener {
                    viewModel.share()
                }
                if (post.likedByMe) {
                    like.setImageResource(R.drawable.ic_liked_24)
                } else {
                    like.setImageResource(R.drawable.ic_like_24)
                }
            }

        }

    }
}