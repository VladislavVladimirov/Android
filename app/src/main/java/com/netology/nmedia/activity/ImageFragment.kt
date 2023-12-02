package com.netology.nmedia.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.netology.nmedia.databinding.FragmentImageBinding
import com.netology.nmedia.util.AndroidUtils
import com.netology.nmedia.util.StringArg
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ImageFragment : Fragment() {
    companion object {
        var Bundle.pictureArg: String? by StringArg
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        val binding = FragmentImageBinding.inflate(inflater, container, false)
        val url = requireNotNull(requireArguments().pictureArg)
        AndroidUtils.loadImage(url, binding.photo)
        binding.back.setOnClickListener {
            findNavController().navigateUp()
        }

        return binding.root
    }
    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
    }
}