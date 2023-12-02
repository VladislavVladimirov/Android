package com.netology.nmedia.adapter.post

import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.netology.nmedia.R
import com.netology.nmedia.adapter.OnInteractionListener
import com.netology.nmedia.databinding.CardPostBinding
import com.netology.nmedia.dto.Post
import com.netology.nmedia.enums.AttachmentType
import com.netology.nmedia.util.AndroidUtils
import com.netology.nmedia.util.Formatter.formatCount
import com.netology.nmedia.util.Formatter.formatPostDate


class PostViewHolder(
    private val binding: CardPostBinding, private val onInteractionListener: OnInteractionListener
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(post: Post) {
        binding.apply {
            author.text = post.author
            published.text = formatPostDate(post.published)
            content.text = post.content
            if (post.link != null) {
                linkHeader.visibility = View.VISIBLE
                linkText.visibility = View.VISIBLE
                linkText.text = post.link
            } else {
                linkHeader.visibility = View.GONE
                linkText.visibility = View.GONE
            }
            like.isChecked = post.likedByMe
            menu.isVisible = post.ownedByMe
            like.text = formatCount(post.likeOwnerIds.size)

            if (post.authorJob != null) {
                authorJob.text = post.authorJob
                authorJob.visibility = View.VISIBLE
            } else {
                authorJob.visibility = View.GONE
            }

            if (post.authorAvatar == null) {
                avatar.setImageResource(R.drawable.ic_avatar_placeholder)
            } else {
                AndroidUtils.loadAvatar(post.authorAvatar, avatar)
            }
            if (post.attachment?.type == AttachmentType.IMAGE) {
                imageAttachment.visibility = View.VISIBLE
                AndroidUtils.loadImage(post.attachment.url, imageAttachment)
            } else {
                imageAttachment.visibility = View.GONE
            }
            if (post.attachment?.type == AttachmentType.AUDIO) {
                audioAttachment.visibility = View.VISIBLE
            } else {
                audioAttachment.visibility = View.GONE
            }
            play.setOnClickListener {
                onInteractionListener.onAudioPlay(post)
            }
            audioAttachmentHeader.setOnClickListener { onInteractionListener.onAudioPlay(post) }

            if (post.attachment?.type == AttachmentType.VIDEO) {
                videoAttachment.visibility = View.VISIBLE
            } else {
                videoAttachment.visibility = View.GONE
            }
            playVideo.setOnClickListener {
                onInteractionListener.onVideoPlay(post)
            }
            videoAttachmentHeader.setOnClickListener { onInteractionListener.onVideoPlay(post) }


            AndroidUtils.loadYouTubePreview(post.content, youtubePlayerPreview)
            like.setOnClickListener {
                onInteractionListener.onPostLike(post)
            }
            share.setOnClickListener {
                onInteractionListener.onPostShare(post)
            }
            menu.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.options_post)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.remove -> {
                                onInteractionListener.onPostRemove(post)
                                true
                            }

                            R.id.edit -> {
                                onInteractionListener.onPostEdit(post)
                                true
                            }

                            else -> false
                        }
                    }
                }.show()
            }

            imageAttachment.setOnClickListener {
                onInteractionListener.onImageClick(post)
            }
            youtubePlayerPreview.setOnClickListener {
                onInteractionListener.onYouTubePlay(post)
            }
            avatar.setOnClickListener {
                onInteractionListener.onAuthorClick(post)
            }
            author.setOnClickListener {
                onInteractionListener.onAuthorClick(post)
            }
            authorJob.setOnClickListener {
                onInteractionListener.onAuthorClick(post)
            }
        }
    }
}

