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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.netology.nmedia.R
import com.netology.nmedia.activity.AudioFragment.Companion.audioArg
import com.netology.nmedia.activity.ImageFragment.Companion.pictureArg
import com.netology.nmedia.activity.VideoFragment.Companion.videoArg
import com.netology.nmedia.adapter.OnInteractionListener
import com.netology.nmedia.adapter.loading.LoadingStateAdapter
import com.netology.nmedia.adapter.post.PostsAdapter
import com.netology.nmedia.databinding.FragmentFeedBinding
import com.netology.nmedia.dto.Post
import com.netology.nmedia.util.AndroidUtils
import com.netology.nmedia.viewmodel.AuthViewModel
import com.netology.nmedia.viewmodel.PostViewModel
import com.netology.nmedia.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class FeedFragment : Fragment() {
    private val viewModel: PostViewModel by activityViewModels()
    private val authViewModel: AuthViewModel by activityViewModels()
    private val userViewModel: UserViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val binding = FragmentFeedBinding.inflate(layoutInflater)
        val adapter = PostsAdapter(object : OnInteractionListener {
            override fun onPostLike(post: Post) {
                if (authViewModel.isAuthorized) {
                    viewModel.likeById(post)
                } else {
                    Snackbar.make(binding.root, R.string.sign_in_error, Snackbar.LENGTH_LONG)
                        .setAction(R.string.sign_in) {
                            findNavController().navigate(R.id.action_feedFragment_to_signInFragment)
                        }.show()
                }
            }

            override fun onPostShare(post: Post) {
                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, post.content)
                    type = "text/plain"
                }
                val shareIntent =
                    Intent.createChooser(intent, getString(R.string.chooser_share_post))
                startActivity(shareIntent)

            }

            override fun onPostRemove(post: Post) {
                viewModel.removeById(post.id)
            }

            override fun onPostEdit(post: Post) {
                viewModel.edit(post)
                findNavController().navigate(R.id.action_feedFragment_to_editPostFragment)
            }

            override fun onYouTubePlay(post: Post) {
                AndroidUtils.extractUrls(post.content).forEach {
                    if (it.contains("youtu")) {
                        val playIntent = Intent(Intent.ACTION_VIEW, Uri.parse(it))
                        if (playIntent.resolveActivity(requireContext().packageManager) != null) {
                            startActivity(playIntent)
                        }
                    }
                }
            }

            override fun onImageClick(post: Post) {
                findNavController().navigate(
                    R.id.action_feedFragment_to_imageFragment,
                    Bundle().apply {
                        pictureArg = post.attachment?.url.toString()
                    })
            }

            override fun onAuthorClick(post: Post) {
                viewLifecycleOwner.lifecycleScope.launch {
                    userViewModel.getUserById(post.authorId).join()
                    findNavController().navigate(R.id.action_feedFragment_to_wallFragment)
                }

            }

            override fun onAudioPlay(post: Post) {
                findNavController().navigate(
                    R.id.action_feedFragment_to_audioFragment,
                    Bundle().apply {
                        audioArg = post.attachment?.url.toString()
                    })
            }
            override fun onVideoPlay(post: Post) {
                findNavController().navigate(
                    R.id.action_feedFragment_to_videoFragment,
                    Bundle().apply {
                        videoArg = post.attachment?.url.toString()
                    })
            }
        })
        activity?.title = getString(R.string.feed_fragment_title)
        var currentMenuProvider: MenuProvider? = null
        authViewModel.authLiveData.observe(viewLifecycleOwner) {
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
                            Snackbar.make(
                                binding.root,
                                R.string.accept_sign_out,
                                Snackbar.LENGTH_LONG
                            )
                                .setAction(R.string.sign_out) {
                                    authViewModel.removeUser()
                                }.show()

                            true
                        }

                        else -> false

                    }
                }
            }.also { currentMenuProvider = it }, viewLifecycleOwner)
        }

        binding.list.adapter = adapter.withLoadStateHeaderAndFooter(
            header = LoadingStateAdapter { adapter.retry() },
            footer = LoadingStateAdapter { adapter.retry() }
        )
        val itemAnimator: DefaultItemAnimator = object : DefaultItemAnimator() {
            override fun canReuseUpdatedViewHolder(viewHolder: RecyclerView.ViewHolder): Boolean {
                return true
            }
        }
        binding.list.itemAnimator = itemAnimator

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                binding.bottomNavigation.selectedItemId = R.id.feed
                viewModel.data.collectLatest(adapter::submitData)
            }
        }

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.feed -> {
                    true
                }

                R.id.profile -> {
                    viewLifecycleOwner.lifecycleScope.launch {
                        val myId = authViewModel.authLiveData.value?.id
                        if (myId != null) {
                            userViewModel.getUserById(myId).join()
                        }
                        findNavController().navigate(R.id.action_feedFragment_to_wallFragment)
                    }
                    true
                }

                R.id.events -> {
                    findNavController().navigate(R.id.action_feedFragment_to_eventFragment)
                    true
                }

                else -> false
            }
        }

        binding.postsSwipeRefresh.setOnRefreshListener { adapter.refresh() }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                adapter.loadStateFlow.collectLatest { state ->
                    binding.postsSwipeRefresh.isRefreshing =
                        state.refresh is LoadState.Loading
                }

            }
        }
        binding.add.setOnClickListener {
            findNavController().navigate(R.id.action_feedFragment_to_newPostFragment)
        }

        return binding.root
    }


}
