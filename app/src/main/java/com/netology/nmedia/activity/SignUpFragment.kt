package com.netology.nmedia.activity

import android.app.Activity

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toFile
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import com.github.dhaval2404.imagepicker.constant.ImageProvider
import com.google.android.material.snackbar.Snackbar
import com.netology.nmedia.R
import com.netology.nmedia.databinding.FragmentSignUpBinding
import com.netology.nmedia.model.PhotoModel
import com.netology.nmedia.util.AndroidUtils
import com.netology.nmedia.viewmodel.PostViewModel
import com.netology.nmedia.viewmodel.SignUpViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpFragment : Fragment() {

    private val viewModel: SignUpViewModel by activityViewModels()
    private val postViewModel: PostViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentSignUpBinding.inflate(inflater, container, false)
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
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            AndroidUtils.hideKeyboard(requireView())
            viewModel.clean()
            findNavController().navigateUp()
        }
        viewModel.dataState.observe(viewLifecycleOwner) { state ->
            binding.loading.isVisible = state.loading
            if (state.error) {
                Snackbar.make(binding.root, R.string.error_loading, Snackbar.LENGTH_LONG).show()
            }
            if (state.success){
                postViewModel.refreshPosts()
                viewModel.clean()
                findNavController().navigateUp()
            }
        }

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
        }
        viewModel.photoState.observe(viewLifecycleOwner) { photoState ->
            if (photoState == null) {
                binding.photoPreviewContainer.isVisible = false
                binding.logo.isVisible = true
                return@observe
            }
            binding.signUp.setOnClickListener {
                AndroidUtils.hideKeyboard(requireView())
                val login = binding.login.text.toString().trim()
                val password = binding.password.text.toString().trim()
                val confirmPassword = binding.confirmPassword.text.toString().trim()
                val name = binding.name.text.toString()
                if (login.isBlank() || password.isBlank() || name.isBlank() || confirmPassword.isBlank()) {
                    Snackbar.make(binding.root,
                        getString(R.string.error_empty_field), Snackbar.LENGTH_LONG).show()
                } else {
                    if (password == confirmPassword) {
                        viewModel.photoState.value?.file?.let { file ->
                            viewModel.signUpWithAvatar(login, password, name,
                                file
                            )
                            viewModel.changePhoto(null)
                        }
                    } else {
                        Snackbar.make(binding.root,
                            getString(R.string.error_confirm_password), Snackbar.LENGTH_LONG).show()
                    }
                }
            }
            binding.photoPreviewContainer.isVisible = true
            binding.logo.isVisible = false
            binding.photoPreview.setImageURI(photoState.uri)
        }
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.changePhoto(null)
    }
}