package com.netology.nmedia.adapter.job

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.netology.nmedia.adapter.OnInteractionListener
import com.netology.nmedia.databinding.CardJobBinding
import com.netology.nmedia.dto.Job


class JobAdapter(
    private val onInteractionListener: OnInteractionListener,
    private val isProfileMine: Boolean
): ListAdapter<Job,JobViewHolder>(JobDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobViewHolder {
        val binding = CardJobBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return JobViewHolder(binding, onInteractionListener, isProfileMine)
    }

    override fun onBindViewHolder(holder: JobViewHolder, position: Int) {
        val job = getItem(position)
        holder.bind(job)
    }
}

