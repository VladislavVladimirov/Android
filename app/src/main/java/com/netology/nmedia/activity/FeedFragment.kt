package com.netology.nmedia.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.netology.nmedia.R
import com.netology.nmedia.activity.EditPostFragment.Companion.textArg
import com.netology.nmedia.adapter.OnInteractionListener
import com.netology.nmedia.adapter.PostsAdapter
import com.netology.nmedia.auth.AppAuth
import com.netology.nmedia.databinding.FragmentFeedBinding
import com.netology.nmedia.dto.Post
import com.netology.nmedia.util.AndroidUtils
import com.netology.nmedia.viewmodel.AuthViewModel
import com.netology.nmedia.viewmodel.PostViewModel


class FeedFragment : Fragment() {
    private val viewModel: PostViewModel by activityViewModels()
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val binding = FragmentFeedBinding.inflate(layoutInflater)

        val adapter = PostsAdapter(object : OnInteractionListener {
            override fun onLike(post: Post) {
                if (authViewModel.isAuthorized) {
                    viewModel.likeById(post)
                } else {
                    Snackbar.make(binding.root, R.string.sign_in_error, Snackbar.LENGTH_LONG)
                        .setAction(R.string.sign_in) {
                            findNavController().navigate(R.id.action_feedFragment_to_signInFragment)
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
            }

            override fun onEdit(post: Post) {
                viewModel.edit(post)
                findNavController().navigate(R.id.action_feedFragment_to_editPostFragment,
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
                findNavController().navigate(
                    R.id.action_feedFragment_to_postFragment,
                    Bundle().apply {
                        textArg = post.id.toString()
                    })
            }

            override fun onRefresh() {
                viewModel.loadPosts()
            }

            override fun onImageClick(post: Post) {
                findNavController().navigate(
                    R.id.action_feedFragment_to_imageFragment,
                    Bundle().apply {
                        textArg = post.attachment?.url.toString()
                    })
            }
        })

        val swipeRefresher = binding.postsSwipeRefresh

        var currentMenuProvider: MenuProvider? = null
        authViewModel.authLiveData.observe(viewLifecycleOwner) { authModel ->
            binding.add.isVisible = authViewModel.isAuthorized
            currentMenuProvider?.let(requireActivity()::removeMenuProvider)
            requireActivity().addMenuProvider(object : MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.auth_menu, menu)
                    menu.setGroupVisible(R.id.authorized, authViewModel.isAuthorized)
                    menu.setGroupVisible(R.id.unauthorized, !authViewModel.isAuthorized)
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                    return when (menuItem.itemId) {
                        R.id.signIn -> {
                            findNavController().navigate(R.id.action_feedFragment_to_signInFragment)
                            true
                        }

                        R.id.signUp -> {
                            findNavController().navigate(R.id.action_feedFragment_to_signUpFragment)
                            true
                        }

                        R.id.signOut -> {
                            Snackbar.make(binding.root, R.string.accept_sign_out, Snackbar.LENGTH_LONG)
                                .setAction(R.string.sign_out) {
                                    AppAuth.getInstance().removeUser()
                                }.show()

                            true
                        }

                        else -> false

                    }
                }
            }.also { currentMenuProvider = it }, viewLifecycleOwner)
        }
        binding.list.adapter = adapter
        viewModel.data.observe(viewLifecycleOwner) { state ->
            adapter.submitList(state.posts)
            binding.emptyText.isVisible = state.empty
        }
        viewModel.dataState.observe(viewLifecycleOwner) { state ->
            binding.progress.isVisible = state.loading
            swipeRefresher.isRefreshing = state.refreshing
            if (state.error) {
                Snackbar.make(binding.root, R.string.error_loading, Snackbar.LENGTH_LONG)
                    .setAction(R.string.retry_loading) { viewModel.loadPosts() }.show()
            }
        }
        viewModel.newer.observe(viewLifecycleOwner) {
            if (it != 0) {
                binding.newPosts.visibility = View.VISIBLE
            }
        }

        binding.add.setOnClickListener {
            findNavController().navigate(R.id.action_feedFragment_to_newPostFragment)
        }

        swipeRefresher.setOnRefreshListener {
            viewModel.refreshPosts()
        }
        binding.newPosts.setOnClickListener {
            viewModel.showHiddenPosts()
            binding.list.smoothScrollToPosition(0)
            it.visibility = View.GONE
        }

        return binding.root
    }

}
