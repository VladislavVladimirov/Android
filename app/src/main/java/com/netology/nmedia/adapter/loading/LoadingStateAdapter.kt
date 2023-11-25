package com.netology.nmedia.adapter.loading

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import com.netology.nmedia.databinding.ItemLoadingBinding

class LoadingStateAdapter(private val retryListener: () -> Unit): LoadStateAdapter<LoadingStateViewHolder>() {
    override fun onBindViewHolder(holder: LoadingStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, loadState: LoadState
    ): LoadingStateViewHolder = LoadingStateViewHolder(
        ItemLoadingBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        retryListener)
}