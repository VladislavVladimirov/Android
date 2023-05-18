package com.netology.nmedia.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels

import androidx.navigation.fragment.findNavController
import com.netology.nmedia.R
import com.netology.nmedia.databinding.FragmentFeedBinding
import com.netology.nmedia.activity.EditPostFragment.Companion.textArg
import com.netology.nmedia.adapter.OnInteractionListener
import com.netology.nmedia.adapter.PostsAdapter
import com.netology.nmedia.dto.Post
import com.netology.nmedia.util.AndroidUtils
import com.netology.nmedia.viewmodel.PostViewModel


class FeedFragment : Fragment() {
    val viewModel: PostViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentFeedBinding.inflate(layoutInflater)

        val adapter = PostsAdapter(object : OnInteractionListener {
            override fun onLike(post: Post) {
                viewModel.likeById(post)
            }

            override fun onShare(post: Post) {
                viewModel.shareById(post.id)
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
            }

            override fun onEdit(post: Post) {
                viewModel.edit(post)
                findNavController().navigate(
                    R.id.action_feedFragment_to_editPostFragment,
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

            override fun onPostClick(post: Post) {
                viewModel.viewPostById(post.id)
                findNavController().navigate(R.id.action_feedFragment_to_postFragment,
                    Bundle().apply {
                        textArg = post.id.toString()
                    })
            }

            override fun onRefresh() {
                viewModel.loadPosts()
            }
        })
        val swipeRefresher =  binding.postsSwipeRefresh

        viewModel.loadPosts()
        binding.list.adapter = adapter
        viewModel.data.observe(viewLifecycleOwner) { state ->
            adapter.submitList(state.posts)
            if (adapter.currentList.size < state.posts.size) {
                binding.list.smoothScrollToPosition(0)
            }
            binding.progress.isVisible = state.loading
            binding.errorGroup.isVisible = state.error
            binding.emptyText.isVisible = state.empty
        }
        binding.add.setOnClickListener {
            findNavController().navigate(R.id.action_feedFragment_to_newPostFragment)
        }
        binding.retryButton.setOnClickListener {
            viewModel.loadPosts()
        }
        swipeRefresher.setOnRefreshListener {
            swipeRefresher.isRefreshing = true
            viewModel.loadPosts()
            swipeRefresher.isRefreshing = false
        }
        return binding.root
    }

}
