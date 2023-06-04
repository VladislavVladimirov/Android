package com.netology.nmedia.adapter

import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.netology.nmedia.R
import com.netology.nmedia.activity.enums.AttachmentType
import com.netology.nmedia.databinding.CardPostBinding
import com.netology.nmedia.dto.Post
import com.netology.nmedia.viewmodel.PostFormatter


class PostViewHolder(
    private val binding: CardPostBinding,
    private val onInteractionListener: OnInteractionListener
) : RecyclerView.ViewHolder(binding.root) {
    private var url: String = ""
    fun bind(post: Post) {

        binding.apply {
            author.text = post.author
            published.text = PostFormatter.formatTime(post.published.toString())
            content.text = post.content
            like.text = PostFormatter.formatCount(post.likes)
            share.text = PostFormatter.formatCount(post.shares)
            views.text = PostFormatter.formatCount(post.views)
            like.isChecked = post.likedByMe

            if (post.authorAvatar == "netology.jpg") {
                url = "http://192.168.0.4:9090/avatars/netology.jpg"
            }
            if (post.authorAvatar == "sber.jpg") {
                url = "http://192.168.0.4:9090/avatars/sber.jpg"
            }
            if (post.authorAvatar == "tcs.jpg") {
                url = "http://192.168.0.4:9090/avatars/tcs.jpg"
            }

            Glide.with(binding.avatar)
                .load(url)
                .placeholder(R.drawable.ic_loading_100dp)
                .error(R.drawable.ic_error_100dp)
                .apply(RequestOptions.bitmapTransform(CircleCrop()))
                .timeout(10_000)
                .into(binding.avatar)

            if (
                (post.content.contains("youtube.com")) ||
                (post.content.contains("youtu.be"))
            ) {
                previewGroup.visibility = View.VISIBLE
            } else {
                previewGroup.visibility = View.GONE
            }
            if (post.attachment?.type == AttachmentType.IMAGE) {
                binding.imageAttachment.contentDescription = post.attachment.description
                Glide.with(binding.imageAttachment)
                    .load("http://192.168.0.4:9090/images/${post.attachment.url}")
                    .timeout(10_000)
                    .into(binding.imageAttachment)
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
                    setOnMenuItemClickListener { item ->
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

            youtubePlayerPreview1.setOnClickListener {
                onInteractionListener.onPlay(post)
            }
            youtubePlayerPreview2.setOnClickListener {
                onInteractionListener.onPlay(post)
            }
        }
    }
}
