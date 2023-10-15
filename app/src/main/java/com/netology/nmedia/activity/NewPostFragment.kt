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
import com.netology.nmedia.di.DependencyContainer
import com.netology.nmedia.model.PhotoModel
import com.netology.nmedia.util.AndroidUtils
import com.netology.nmedia.util.StringArg
import com.netology.nmedia.viewmodel.PostViewModel
import com.netology.nmedia.viewmodel.ViewModelFactory


class NewPostFragment : Fragment() {
    companion object {
        var Bundle.textArg: String? by StringArg
    }

    private val viewModel: PostViewModel by activityViewModels(
        factoryProducer = {
            ViewModelFactory(
                dependencyContainer.repository,
                dependencyContainer.draftRepository,
                dependencyContainer.appAuth,
                dependencyContainer.apiService
            )
        }
    )
    private val dependencyContainer = DependencyContainer.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentNewPostBinding.inflate(inflater, container, false)
        val localDraft = viewModel.getDraft()

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
        binding.edit.setText(localDraft)


        activity?.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.new_post_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.save -> {
                        viewModel.changeContent(binding.edit.text.toString())
                        viewModel.save()
                        viewModel.clearDraft()
                        AndroidUtils.hideKeyboard(requireView())
                        true
                    }

                    else -> false
                }
            }

        }, viewLifecycleOwner)

        arguments?.textArg?.let(binding.edit::setText)

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
            viewModel.saveDraft(binding.edit.text.toString())
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
            viewModel.loadPosts()
            findNavController().navigateUp()
        }
        return binding.root
    }
}