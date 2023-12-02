package com.netology.nmedia.adapter.event

import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.netology.nmedia.R
import com.netology.nmedia.adapter.OnInteractionListener
import com.netology.nmedia.databinding.CardEventBinding
import com.netology.nmedia.dto.Event
import com.netology.nmedia.enums.AttachmentType
import com.netology.nmedia.enums.EventType
import com.netology.nmedia.util.AndroidUtils
import com.netology.nmedia.util.Formatter

class EventViewHolder(
    private val binding: CardEventBinding,
    private val onInteractionListener: OnInteractionListener
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(event: Event) {
        binding.apply {
            author.text = event.author
            published.text = Formatter.formatPostDate(event.published)
            content.text = event.content
            if (event.link != null) {
                linkHeader.visibility = View.VISIBLE
                linkText.visibility = View.VISIBLE
                linkText.text = event.link
            } else {
                linkHeader.visibility = View.GONE
                linkText.visibility = View.GONE
            }
            like.isChecked = event.likedByMe
            participate.isChecked = event.participatedByMe
            participate.text = Formatter.formatCount(event.participantsIds.size)
            if (event.participatedByMe) {
                youParticipate.visibility = View.VISIBLE
            } else {
                youParticipate.visibility = View.GONE
            }

            menu.isVisible = event.ownedByMe

            like.text = Formatter.formatCount(event.likeOwnerIds.size)

            if (event.authorJob != null) {
                authorJob.text = event.authorJob
                authorJob.visibility = View.VISIBLE
            } else {
                authorJob.visibility = View.GONE
            }
            dateOfEvent.text = Formatter.formatEventDate(event.datetime)
            when (event.type) {
                EventType.ONLINE -> {
                    onlineOrOffline.text = "Онлайн"
                }

                EventType.OFFLINE -> {
                    onlineOrOffline.text = "Оффлайн"
                }
            }

            if (event.authorAvatar == null) {
                avatar.setImageResource(R.drawable.ic_avatar_placeholder)
            } else {
                AndroidUtils.loadAvatar(event.authorAvatar, avatar)
            }
            if (event.attachment?.type == AttachmentType.IMAGE) {
                imageAttachment.visibility = View.VISIBLE
                AndroidUtils.loadImage(event.attachment.url, imageAttachment)
            } else {
                imageAttachment.visibility = View.GONE
            }
            if (event.attachment?.type == AttachmentType.AUDIO) {
                audioAttachment.visibility = View.VISIBLE
            } else {
                audioAttachment.visibility = View.GONE
            }
            play.setOnClickListener {
                onInteractionListener.onAudioPlay(event)
            }
            audioAttachmentHeader.setOnClickListener { onInteractionListener.onAudioPlay(event) }

            if (event.attachment?.type == AttachmentType.VIDEO) {
                videoAttachment.visibility = View.VISIBLE
            } else {
                videoAttachment.visibility = View.GONE
            }
            playVideo.setOnClickListener {
                onInteractionListener.onVideoPlay(event)
            }
            videoAttachmentHeader.setOnClickListener { onInteractionListener.onVideoPlay(event) }
            AndroidUtils.loadYouTubePreview(event.content, youtubePlayerPreview)
            like.setOnClickListener {
                onInteractionListener.onEventLike(event)
            }
            participate.setOnClickListener {
                onInteractionListener.onEventTakePart(event)
            }
            share.setOnClickListener {
                onInteractionListener.onEventShare(event)
            }
            menu.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.options_post)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.remove -> {
                                onInteractionListener.onEventRemove(event)
                                true
                            }

                            R.id.edit -> {
                                onInteractionListener.onEventEdit(event)
                                true
                            }

                            else -> false
                        }
                    }
                }.show()
            }
            imageAttachment.setOnClickListener {
                onInteractionListener.onImageClickEvent(event)
            }
            youtubePlayerPreview.setOnClickListener {
                onInteractionListener.onYouTubePlay(event)
            }
            avatar.setOnClickListener {
                onInteractionListener.onAuthorClickEvent(event)
            }
            author.setOnClickListener {
                onInteractionListener.onAuthorClickEvent(event)
            }
            authorJob.setOnClickListener {
                onInteractionListener.onAuthorClickEvent(event)
            }
        }
    }

}