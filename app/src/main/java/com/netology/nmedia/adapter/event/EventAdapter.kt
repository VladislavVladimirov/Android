package com.netology.nmedia.adapter.event

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import com.netology.nmedia.adapter.OnInteractionListener
import com.netology.nmedia.databinding.CardEventBinding
import com.netology.nmedia.dto.Event

class EventAdapter(
    private val onInteractionListener: OnInteractionListener
) : PagingDataAdapter<Event, EventViewHolder>(EventDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding = CardEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EventViewHolder(binding, onInteractionListener)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = getItem(position) ?:return
        holder.bind(event)
    }
}