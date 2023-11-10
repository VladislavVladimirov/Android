package com.netology.nmedia.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.netology.nmedia.R
import com.netology.nmedia.activity.EditPostFragment.Companion.textArg
import com.netology.nmedia.adapter.OnInteractionListener
import com.netology.nmedia.adapter.PostViewHolder
import com.netology.nmedia.databinding.FragmentPostBinding
import com.netology.nmedia.dto.Post
import com.netology.nmedia.util.AndroidUtils
import com.netology.nmedia.viewmodel.AuthViewModel
import com.netology.nmedia.viewmodel.PostViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PostFragment : Fragment() {
    private val viewModel: PostViewModel by activityViewModels()
    private val authViewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentPostBinding.inflate(inflater, container, false)
        val swipeRefresher = binding.postSwipeRefresh
        val listener = object : OnInteractionListener {
            override fun onLike(post: Post) {
                if (authViewModel.isAuthorized) {
                    viewModel.likeById(post)
                } else {
                    Snackbar.make(
                        binding.root,
                        R.string.sign_in_error,
                        Snackbar.LENGTH_LONG
                    )
                        .setAction(R.string.sign_in) {
                            findNavController().navigate(R.id.action_postFragment_to_signInFragment)
                        }.show()
                }
            }

            override fun onShare(post: Post) {
                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, post.content)
                    type = "text/plain"
                }
                val shareIntent =
                    Intent.createChooser(intent, getString(R.string.chooser_share_post))
                startActivity(shareIntent)
            }

            override fun onRemove(post: Post) {
                viewModel.removeById(post.id)
                findNavController().navigateUp()
            }

            override fun onEdit(post: Post) {
                viewModel.edit(post)
                findNavController().navigate(
                    R.id.action_postFragment_to_editPostFragment,
                    Bundle().apply {
                        textArg = post.content
                    })
            }

            override fun onPlay(post: Post) {
                AndroidUtils.extractUrls(post.content).forEach {
                    if (it.contains("youtu")) {
                        val playIntent = Intent(Intent.ACTION_VIEW, Uri.parse(it))
                        if (playIntent.resolveActivity(requireContext().packageManager) != null) {
                            startActivity(playIntent)
                        }
                    }
                }
            }

            override fun onRefresh() {
                viewModel.loadPosts()
            }

        }
        val id = requireNotNull(requireArguments().textArg).toInt()
        binding.postContent.apply {
            viewModel.data.observe(viewLifecycleOwner) { it ->
                val viewHolder = PostViewHolder(binding.postContent, listener)
                val post = it.posts.find { it.id == id }
                post?.let {
                    viewHolder.bind(post)
                    imageAttachment.setOnClickListener {
                        findNavController().navigate(
                            R.id.action_postFragment_to_imageFragment,
                            Bundle().apply {
                                textArg = post.attachment?.url.toString()
                            })
                    }
                }
            }
            viewModel.dataState.observe(viewLifecycleOwner) {
                binding.progress.isVisible = it.loading
                binding.postToHide.isGone = it.error
                swipeRefresher.isRefreshing = it.refreshing
                if (it.error) {
                    Snackbar.make(
                        binding.root,
                        R.string.error_loading,
                        Snackbar.LENGTH_LONG
                    )
                        .setAction(R.string.retry_loading) { viewModel.loadPosts() }
                        .show()
                }
            }
        }
        swipeRefresher.setOnRefreshListener {
            viewModel.refreshPosts()
        }
        return binding.root
    }

}