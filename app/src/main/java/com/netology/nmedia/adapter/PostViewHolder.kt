package com.netology.nmedia.adapter

import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.netology.MyApp.R
import com.netology.MyApp.databinding.CardPostBinding
import com.netology.nmedia.dto.Post
import com.netology.nmedia.viewmodel.PostFormatter

class PostViewHolder(
    private val binding: CardPostBinding,
    private val onInteractionListener: OnInteractionListener
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(post: Post) {
        binding.apply {
            author.text = post.author
            published.text = post.published
            content.text = post.content
            avatar.setImageResource(post.authorAvatar)
            like.text = PostFormatter.formatCount(post.likes)
            share.text = PostFormatter.formatCount(post.shares)
            views.text = PostFormatter.formatCount(post.views)
            like.isChecked = post.likedByMe
            if (
                (post.content.contains("m.youtube.com")) ||
                (post.content.contains("www.youtube.com")) ||
                (post.content.contains("youtu.be"))
            ) {
                previewGroup.visibility = View.VISIBLE
            } else {
                previewGroup.visibility = View.GONE
            }
            like.setOnClickListener {
                onInteractionListener.onLike(post)
            }
            share.setOnClickListener {
                onInteractionListener.onShare(post)
            }

            menu.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.options_post)
                    setOnMenuItemClickListener {item ->
                        when (item.itemId) {
                            R.id.remove -> {
                                onInteractionListener.onRemove(post)
                                true
                            }
                            R.id.edit -> {
                                onInteractionListener.onEdit(post)
                                true
                            }
                            else -> false
                        }
                    }
                }.show()
            }
            root.setOnClickListener {
                onInteractionListener.onPostClick(post)
            }
            youtubePlayerPreview1.setOnClickListener{
                onInteractionListener.onPlay(post)
            }
            youtubePlayerPreview2.setOnClickListener{
                onInteractionListener.onPlay(post)
            }
        }
    }
}
