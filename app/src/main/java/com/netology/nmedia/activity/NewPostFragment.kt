package com.netology.nmedia.activity

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toFile
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import com.github.dhaval2404.imagepicker.constant.ImageProvider
import com.google.android.material.snackbar.Snackbar
import com.netology.nmedia.R
import com.netology.nmedia.databinding.FragmentNewPostBinding
import com.netology.nmedia.model.media.PhotoModel
import com.netology.nmedia.util.AndroidUtils
import com.netology.nmedia.viewmodel.PostViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class NewPostFragment : Fragment() {
    private val viewModel: PostViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentNewPostBinding.inflate(inflater, container, false)
        val draftContent = viewModel.getDraftContent()
        val draftLink = viewModel.getDraftLink()

        val pickPhotoLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                when (it.resultCode) {
                    ImagePicker.RESULT_ERROR -> {
                        Snackbar.make(
                            binding.root,
                            ImagePicker.getError(it.data),
                            Snackbar.LENGTH_LONG
                        ).show()
                    }

                    Activity.RESULT_OK -> {
                        val uri: Uri? = it.data?.data
                        val file = uri?.toFile()

                        viewModel.changePhoto(PhotoModel(uri, file))
                    }
                }
            }
        activity?.title = getString(R.string.new_post)
        binding.edit.setText(draftContent)
        binding.linkText.setText(draftLink)
        if (binding.linkText.text.isNotBlank()) {
            binding.linkText.visibility = View.VISIBLE
            binding.deleteLink.visibility = View.VISIBLE
        }

        binding.addLink.setOnClickListener {
            binding.linkText.visibility = View.VISIBLE
            binding.deleteLink.visibility = View.VISIBLE
        }
        binding.deleteLink.setOnClickListener {
            binding.linkText.text.clear()
            binding.linkText.visibility = View.GONE
            binding.deleteLink.visibility = View.GONE

        }


        activity?.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.new_post_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.save -> {
                        if (binding.edit.text.isNotBlank()) {
                            viewModel.changeContent(binding.edit.text.toString(), binding.linkText.text.toString())
                            viewModel.save()
                            viewModel.clearDrafts()
                            AndroidUtils.hideKeyboard(requireView())
                            true
                        } else {
                            Snackbar.make(
                                binding.root,
                                getString(R.string.error_empty_content), Snackbar.LENGTH_LONG
                            ).show()
                            false
                        }
                    }

                    else -> false
                }
            }

        }, viewLifecycleOwner)

        binding.takePhoto.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(2048)
                .provider(ImageProvider.CAMERA)
                .createIntent(pickPhotoLauncher::launch)
        }
        binding.pickPhotoFromGallery.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(2048)
                .provider(ImageProvider.GALLERY)
                .galleryMimeTypes(
                    arrayOf(
                        "image/png",
                        "image/jpeg",
                    )
                )
                .createIntent(pickPhotoLauncher::launch)
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            viewModel.saveDraftContent(binding.edit.text.toString())
            viewModel.saveDraftLink(binding.linkText.text.toString())
            AndroidUtils.hideKeyboard(requireView())
            findNavController().navigateUp()
        }
        binding.removePhoto.setOnClickListener {
            viewModel.changePhoto(null)
        }

        viewModel.photoState.observe(viewLifecycleOwner) { photoState ->
            if (photoState == null) {
                binding.photoPreviewContainer.isVisible = false

                return@observe
            }
            binding.photoPreviewContainer.isVisible = true

            binding.photoPreview.setImageURI(photoState.uri)
        }

        viewModel.postCreated.observe(viewLifecycleOwner) {
            findNavController().navigateUp()
        }
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.changePhoto(null)
    }
}