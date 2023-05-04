package com.netology.nmedia.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.netology.MyApp.R
import com.netology.MyApp.databinding.FragmentFeedBinding
import com.netology.nmedia.activity.EditPostFragment.Companion.textArg
import com.netology.nmedia.adapter.OnInteractionListener
import com.netology.nmedia.adapter.PostsAdapter
import com.netology.nmedia.dto.Post
import com.netology.nmedia.viewmodel.PostViewModel


class FeedFragment : Fragment() {
    val viewModel: PostViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentFeedBinding.inflate(layoutInflater)
        val adapter = PostsAdapter(object : OnInteractionListener {
            override fun onLike(post: Post) {
                viewModel.likeById(post.id)
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
                val playIntent = Intent(Intent.ACTION_VIEW, Uri.parse(post.content))
                if (playIntent.resolveActivity(requireContext().packageManager) != null) {
                    startActivity(playIntent)
                }
            }

            override fun onPostClick(post: Post) {
                viewModel.viewPostById(post.id)
                findNavController().navigate(R.id.action_feedFragment_to_postFragment,
                    Bundle().apply {
                        textArg = post.id.toString()
                    })
            }
        })

        binding.list.adapter = adapter
        viewModel.data.observe(viewLifecycleOwner) { posts ->
            adapter.submitList(posts)
        }
        binding.add.setOnClickListener {
            viewModel.cancelEdit()
            findNavController().navigate(R.id.action_feedFragment_to_newPostFragment)
        }
        return binding.root
    }
}
