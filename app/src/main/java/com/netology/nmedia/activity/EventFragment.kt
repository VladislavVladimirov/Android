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
import com.netology.nmedia.adapter.event.EventAdapter
import com.netology.nmedia.adapter.loading.LoadingStateAdapter
import com.netology.nmedia.databinding.FragmentEventBinding
import com.netology.nmedia.dto.Event
import com.netology.nmedia.util.AndroidUtils
import com.netology.nmedia.viewmodel.AuthViewModel
import com.netology.nmedia.viewmodel.EventViewModel
import com.netology.nmedia.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class EventFragment : Fragment() {
    private val viewModel: EventViewModel by activityViewModels()
    private val authViewModel: AuthViewModel by activityViewModels()
    private val userViewModel: UserViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentEventBinding.inflate(layoutInflater)
        val adapter = EventAdapter(object : OnInteractionListener {
            override fun onEventLike(event: Event) {
                if (authViewModel.isAuthorized) {
                    viewModel.likeById(event)
                } else {
                    Snackbar.make(binding.root, R.string.sign_in_error, Snackbar.LENGTH_LONG)
                        .setAction(R.string.sign_in) {
                            findNavController().navigate(R.id.action_feedFragment_to_signInFragment)
                        }.show()
                }
            }

            override fun onEventTakePart(event: Event) {
                if (authViewModel.isAuthorized) {
                    viewModel.takePartById(event)
                } else {
                    Snackbar.make(binding.root, R.string.sign_in_error, Snackbar.LENGTH_LONG)
                        .setAction(R.string.Sign_in_to_take_part) {
                            findNavController().navigate(R.id.action_feedFragment_to_signInFragment)
                        }.show()
                }
            }

            override fun onEventShare(event: Event) {
                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, event.content)
                    type = "text/plain"
                }
                val shareIntent =
                    Intent.createChooser(intent, getString(R.string.chooser_share_post))
                startActivity(shareIntent)
            }

            override fun onEventRemove(event: Event) {
                viewModel.removeById(event.id)
            }

            override fun onEventEdit(event: Event) {
                viewModel.edit(event)
                findNavController().navigate(R.id.action_eventFragment_to_editEventFragment)
            }

            override fun onYouTubePlay(event: Event) {
                AndroidUtils.extractUrls(event.content).forEach {
                    if (it.contains("youtu")) {
                        val playIntent = Intent(Intent.ACTION_VIEW, Uri.parse(it))
                        if (playIntent.resolveActivity(requireContext().packageManager) != null) {
                            startActivity(playIntent)
                        }
                    }
                }
            }

            override fun onImageClickEvent(event: Event) {
                findNavController().navigate(
                    R.id.action_eventFragment_to_imageFragment,
                    Bundle().apply {
                        pictureArg = event.attachment?.url.toString()
                    })
            }

            override fun onAuthorClickEvent(event: Event) {
                viewLifecycleOwner.lifecycleScope.launch {
                    userViewModel.getUserById(event.authorId).join()
                    findNavController().navigate(R.id.action_feedFragment_to_wallFragment)
                }
            }
            override fun onAudioPlay(event: Event) {
                findNavController().navigate(
                    R.id.action_eventFragment_to_audioFragment,
                    Bundle().apply {
                        audioArg = event.attachment?.url.toString()
                    })
            }
            override fun onVideoPlay(event: Event) {
                findNavController().navigate(
                    R.id.action_eventFragment_to_videoFragment,
                    Bundle().apply {
                        videoArg = event.attachment?.url.toString()
                    })
            }
        })
        activity?.title = getString(R.string.events)
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
                            findNavController().navigate(R.id.action_eventFragment_to_signInFragment)
                            true
                        }

                        R.id.signUp -> {
                            findNavController().navigate(R.id.action_eventFragment_to_signUpFragment)
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
                binding.bottomNavigation.selectedItemId = R.id.events
                viewModel.data.collectLatest(adapter::submitData)
            }
        }
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.feed -> {
                    findNavController().navigate(R.id.action_eventFragment_to_feedFragment)
                    true
                }

                R.id.profile -> {
                    viewLifecycleOwner.lifecycleScope.launch {
                        val myId = authViewModel.authLiveData.value?.id
                        if (myId != null) {
                            userViewModel.getUserById(myId).join()
                        }
                        findNavController().navigate(R.id.action_eventFragment_to_wallFragment)
                    }
                    true
                }

                R.id.events -> {
                    true
                }

                else -> false
            }

        }
        binding.eventsSwipeRefresh.setOnRefreshListener { adapter.refresh() }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                adapter.loadStateFlow.collectLatest { state ->
                    binding.eventsSwipeRefresh.isRefreshing =
                        state.refresh is LoadState.Loading
                }
            }
        }
        binding.add.setOnClickListener {
            findNavController().navigate(R.id.action_eventFragment_to_newEventFragment)
        }
        return binding.root
    }
}