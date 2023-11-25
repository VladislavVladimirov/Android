package com.netology.nmedia.adapter.job

import android.view.View
import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.netology.nmedia.R
import com.netology.nmedia.adapter.OnInteractionListener
import com.netology.nmedia.databinding.CardJobBinding
import com.netology.nmedia.dto.Job
import com.netology.nmedia.util.Formatter.formatJobDate

class JobViewHolder(
    private val binding: CardJobBinding,
    private val onInteractionListener: OnInteractionListener,
    private val isProfileMine: Boolean
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(job: Job) {
        binding.apply {
            workName.text = job.name
            userPosition.text = job.position
            startWork.text = formatJobDate(job.start)
            finishWork.text =  formatJobDate(job.finish)

            if (job.link != null) {
                link.visibility = View.VISIBLE
                link.text = job.link
            } else {
                link.visibility = View.GONE
            }
            menu.isVisible = isProfileMine
            menu.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.options_job)
                    setOnMenuItemClickListener { item->
                        when (item.itemId) {
                            R.id.remove -> {
                                onInteractionListener.onJobRemove(job)
                                true
                            }
                            R.id.edit -> {onInteractionListener.onJobEdit(job)
                                true
                            }
                            else -> false
                        }
                    }
                }.show()
            }
        }
    }
}