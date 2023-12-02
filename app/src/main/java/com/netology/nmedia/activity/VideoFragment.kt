package com.netology.nmedia.activity

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.netology.nmedia.databinding.FragmentVideoBinding
import com.netology.nmedia.util.StringArg
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VideoFragment : Fragment() {
    companion object {
        var Bundle.videoArg: String? by StringArg
        private var mediaPlayer: MediaPlayer? = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentVideoBinding.inflate(inflater, container, false)
        val url = requireNotNull(requireArguments().videoArg)

        binding.apply {
         videoView.setVideoURI(url.toUri())
            play.setOnClickListener {
                play.visibility = View.GONE
                videoView.apply {
                    setMediaController(MediaController(context))
                    setVideoURI(Uri.parse(url))
                    setOnPreparedListener {
                        start()
                    }
                    setOnCompletionListener {
                        stopPlayback()
                        play.visibility = View.VISIBLE
                    }
                }
            }
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
    }

    override fun onStop() {
        super.onStop()
        mediaPlayer?.release()
        mediaPlayer = null
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
    }
}