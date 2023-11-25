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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState
import com.google.android.material.snackbar.Snackbar
import com.netology.nmedia.R
import com.netology.nmedia.activity.ImageFragment.Companion.pictureArg
import com.netology.nmedia.adapter.OnInteractionListener
import com.netology.nmedia.adapter.job.JobAdapter
import com.netology.nmedia.adapter.loading.LoadingStateAdapter
import com.netology.nmedia.adapter.post.PostsAdapter
import com.netology.nmedia.databinding.FragmentWallBinding
import com.netology.nmedia.dto.Job
import com.netology.nmedia.dto.Post
import com.netology.nmedia.util.AndroidUtils
import com.netology.nmedia.viewmodel.AuthViewModel
import com.netology.nmedia.viewmodel.PostViewModel
import com.netology.nmedia.viewmodel.UserViewModel
import com.netology.nmedia.viewmodel.WallViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagingApi::class)
@ExperimentalCoroutinesApi
class WallFragment : Fragment() {
    private val postViewModel: PostViewModel by activityViewModels()
    private val authViewModel: AuthViewModel by activityViewModels()
    private val wallViewModel: WallViewModel by activityViewModels()
    private val userViewModel: UserViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentWallBinding.inflate(inflater, container, false)

        val authorAvatar = userViewModel.user.value?.avatar
        val name = userViewModel.user.value?.name
        val authorId = userViewModel.user.value?.id ?: 0
        val myId = authViewModel.authLiveData.value?.id

        val postAdapter = PostsAdapter(object : OnInteractionListener {
            override fun onPostLike(post: Post) {
                if (authViewModel.isAuthorized) {
                    postViewModel.likeById(post)
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
                postViewModel.removeById(post.id)
            }

            override fun onPostEdit(post: Post) {
                postViewModel.edit(post)
                findNavController().navigate(R.id.action_wallFragment_to_editPostFragment)

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


            override fun onImageClick(post: Post) {
                findNavController().navigate(
                    R.id.action_feedFragment_to_imageFragment,
                    Bundle().apply {
                        pictureArg = post.attachment?.url.toString()
                    })
            }
        })

        val jobAdapter = JobAdapter(object : OnInteractionListener {
            override fun onJobEdit(job: Job) {
                wallViewModel.edit(job)
                findNavController()
                    .navigate(R.id.action_wallFragment_to_newJobFragment)
            }

            override fun onJobRemove(job: Job) {
                wallViewModel.removeJobById(job.id)
            }
        }, myId == authorId)

        wallViewModel.loadJobsById(authorId)
        wallViewModel.getLatestPostsById(authorId)
        activity?.title = getString(R.string.wall_fragment_title)
        binding.apply {
            if (authorAvatar != null) {
                AndroidUtils.loadAvatar(authorAvatar, profile.avatar)
            } else {
                profile.avatar.setImageResource(R.drawable.ic_avatar_placeholder)
            }
            profile.userName.text = name

            if (myId == authorId) {
                profile.addJob.visibility = View.VISIBLE
                profile.addJob.setOnClickListener {
                    findNavController().navigate(R.id.action_wallFragment_to_newJobFragment)
                }
            } else {
                profile.addJob.visibility = View.GONE
            }
            profile.listJob.adapter = jobAdapter
            list.adapter = postAdapter.withLoadStateFooter(
                footer = LoadingStateAdapter { postAdapter.retry() }
            )
            add.isVisible = authViewModel.isAuthorized && myId == authorId
            add.setOnClickListener {
                findNavController().navigate(R.id.action_wallFragment_to_newPostFragment)
            }

        }
        viewLifecycleOwner.lifecycleScope.launch {
            wallViewModel.getPosts(authorId).collectLatest(postAdapter::submitData)
        }
        wallViewModel.getJobs().observe(viewLifecycleOwner) {
            jobAdapter.submitList(it)
        }
        binding.swipeRefresh.setOnRefreshListener {
            postAdapter.refresh()
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                postAdapter.loadStateFlow.collectLatest { state ->
                    binding.swipeRefresh.isRefreshing =
                        state.refresh is LoadState.Loading
                }
            }
        }

        return binding.root
    }


}