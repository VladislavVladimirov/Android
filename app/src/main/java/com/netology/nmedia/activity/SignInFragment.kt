package com.netology.nmedia.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.netology.nmedia.R
import com.netology.nmedia.databinding.FragmentSignInBinding
import com.netology.nmedia.util.AndroidUtils
import com.netology.nmedia.viewmodel.SignInViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignInFragment : Fragment() {

    private val viewModel: SignInViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentSignInBinding.inflate(inflater, container, false)
        activity?.title = getString(R.string.sign_in_title)
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
            if (state.success) {
                viewModel.clean()
                findNavController().navigateUp()
            }
        }
        binding.ok.setOnClickListener {
            AndroidUtils.hideKeyboard(requireView())
            val login = binding.login.text.toString().trim()
            val password = binding.password.text.toString().trim()
            if (login.isBlank() || password.isBlank()) {
                Snackbar.make(
                    binding.root,
                    R.string.error_empty_login_and_pass,
                    Snackbar.LENGTH_LONG
                ).show()
            } else {
                viewModel.signIn(login, password)
            }
        }
        binding.signUp.setOnClickListener {
            AndroidUtils.hideKeyboard(requireView())
            findNavController().navigate(R.id.action_signInFragment_to_signUpFragment)
        }


        return binding.root
    }
}