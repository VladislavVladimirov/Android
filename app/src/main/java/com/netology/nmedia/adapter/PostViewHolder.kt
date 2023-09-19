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
import com.netology.nmedia.util.AndroidUtils
import com.netology.nmedia.viewmodel.PostFormatter
import java.lang.Exception


class PostViewHolder(
    private val binding: CardPostBinding,
    private val onInteractionListener: OnInteractionListener
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(post: Post) {

        binding.apply {
            author.text = post.author
            published.text = PostFormatter.formatTime(post.published)
            content.text = post.content
            like.text = PostFormatter.formatCount(post.likes)
            share.text = PostFormatter.formatCount(post.shares)
            views.text = PostFormatter.formatCount(post.views)
            like.isChecked = post.likedByMe


            Glide.with(avatar)
                .load("http://192.168.0.4:9999/avatars/${post.authorAvatar}")
                .placeholder(R.drawable.ic_loading_100dp)
                .error(R.drawable.ic_error_100dp)
                .apply(RequestOptions.bitmapTransform(CircleCrop()))
                .timeout(10_000)
                .into(avatar)

            if (post.attachment?.type == AttachmentType.IMAGE) {
                imageAttachment.visibility = View.VISIBLE
                imageAttachment.contentDescription = post.attachment.description
                Glide.with(imageAttachment)
                    .load("http://192.168.0.4:9999/images/${post.attachment.url}")
                    .timeout(10_000)
                    .into(imageAttachment)
            }  else {
                imageAttachment.visibility = View.GONE
            }

            AndroidUtils.extractUrls(post.content)
                // Ищем первую ссылку на ютуб
                .find {
                    it.contains("youtu")
                }
                // Если нашли
                ?.also {
                    lateinit var videoId: String
                    youtubePlayerPreview.visibility = View.VISIBLE
                    try {
                        if (it.contains("youtu.be")) {
                            videoId = it.split(".be/")[1]
                            val videoPreviewUrl =
                                "http://img.youtube.com/vi/$videoId/maxresdefault.jpg"
                            Glide.with(youtubePlayerPreview)
                                .load(videoPreviewUrl)
                                .timeout(10_000)
                                .into(youtubePlayerPreview)
                        }
                        videoId = it.split("v=")[1]
                        val videoPreviewUrl = "http://img.youtube.com/vi/$videoId/maxresdefault.jpg"
                        Glide.with(youtubePlayerPreview)
                            .load(videoPreviewUrl)
                            .timeout(10_000)
                            .into(youtubePlayerPreview)
                    } catch (e: Exception) {
                        println("Error!")
                    }
                }
            // Если не нашли
                ?: run {
                    youtubePlayerPreview.visibility = View.GONE
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

            youtubePlayerPreview.setOnClickListener {
                onInteractionListener.onPlay(post)
            }
        }
    }
}
