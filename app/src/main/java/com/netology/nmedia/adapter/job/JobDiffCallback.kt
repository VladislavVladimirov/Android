package com.netology.nmedia.adapter.job

import androidx.recyclerview.widget.DiffUtil
import com.netology.nmedia.dto.Job

class JobDiffCallback : DiffUtil.ItemCallback<Job>() {
    override fun areItemsTheSame(oldJob: Job, newJob: Job): Boolean {
        return oldJob.id == newJob.id
    }

    override fun areContentsTheSame(oldItem: Job, newItem: Job): Boolean {
        return oldItem == newItem
    }
}

