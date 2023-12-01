package com.netology.nmedia.adapter.event

import androidx.recyclerview.widget.DiffUtil
import com.netology.nmedia.dto.Event

class EventDiffCallback : DiffUtil.ItemCallback<Event>() {
    override fun areItemsTheSame(oldEvent: Event, newEvent: Event): Boolean {
        return oldEvent.id == newEvent.id
    }

    override fun areContentsTheSame(oldItem: Event, newItem: Event): Boolean {
        return oldItem == newItem
    }
}