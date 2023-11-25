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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toFile
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import com.github.dhaval2404.imagepicker.constant.ImageProvider
import com.google.android.material.snackbar.Snackbar
import com.netology.nmedia.R
import com.netology.nmedia.databinding.FragmentEditPostBinding
import com.netology.nmedia.model.media.PhotoModel
import com.netology.nmedia.util.AndroidUtils
import com.netology.nmedia.viewmodel.PostViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class EditPostFragment : Fragment() {
    private val viewModel: PostViewModel by activityViewModels(
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentEditPostBinding.inflate(inflater, container, false)
        val editedPost = viewModel.getEditedPost()

        activity?.title = getString(R.string.description_edit_post)

        binding.edit.setText(editedPost?.content)
        binding.linkText.setText(editedPost?.link)

        val attachment = editedPost?.attachment
        if (attachment != null) {
            viewModel.changePhoto(PhotoModel(Uri.parse(attachment.url), file = null))
        }
        if (attachment?.url != null) {
            binding.photoPreviewContainer.visibility = View.VISIBLE
            binding.photoPreview.setImageURI(Uri.parse(attachment.url))
//            AndroidUtils.loadImage(url = attachment.url, imageView = binding.photoPreview)
        } else {
            binding.photoPreviewContainer.visibility = View.GONE
        }
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
        activity?.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.edit_post_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.save -> {
                        if (binding.edit.text.isNotBlank()) {
                            viewModel.changeContent(
                                binding.edit.text.toString(),
                                binding.linkText.text.toString()
                            )
                            viewModel.save()
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

                    R.id.cancel -> {
                        AndroidUtils.hideKeyboard(requireView())
                        findNavController().navigateUp()
                        true
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
        binding.removePhoto.setOnClickListener {
            viewModel.changePhoto(null)
            viewModel.changeAttachmentPhoto("")
            binding.photoPreviewContainer.visibility = View.GONE

        }
        viewModel.photoState.observe(viewLifecycleOwner) { photoState ->
            if (photoState == null) {
                binding.photoPreviewContainer.visibility = View.GONE
                binding.photoPreview.setImageURI(null)
                return@observe
            }
            binding.photoPreviewContainer.visibility = View.VISIBLE
            binding.photoPreview.setImageURI(photoState.uri)
        }
        viewModel.postCreated.observe(viewLifecycleOwner) {

            binding.photoPreview.setImageURI(null)
            findNavController().navigateUp()
        }
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.clear()
        viewModel.changePhoto(null)
    }
}
