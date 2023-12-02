package com.netology.nmedia.activity

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.netology.nmedia.databinding.FragmentAudioBinding
import com.netology.nmedia.util.StringArg
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AudioFragment : Fragment() {
    companion object {
        var Bundle.audioArg: String? by StringArg
        private var mediaPlayer: MediaPlayer? = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentAudioBinding.inflate(inflater, container, false)
        val url = requireNotNull(requireArguments().audioArg)

        binding.apply {
            play.setOnClickListener {
                if (mediaPlayer == null) {
                    mediaPlayer = MediaPlayer.create(context, url.toUri())
                    audioBar.max = mediaPlayer!!.duration
                    val handler = Handler(Looper.getMainLooper())
                    handler.postDelayed(object : Runnable {
                        override fun run() {
                            try {
                                binding.audioBar.progress = mediaPlayer!!.currentPosition
                                handler.postDelayed(this, 1000)
                            } catch (e: Exception) {
                                binding.audioBar.progress = 0
                            }
                        }
                    }, 0)
                    mediaPlayer?.start()
                } else {
                    if (mediaPlayer!!.isPlaying) mediaPlayer?.pause() else mediaPlayer?.start()
                }
            }
            binding.stop.setOnClickListener {
                if (mediaPlayer != null) {
                    mediaPlayer?.release()
                    mediaPlayer = null
                }
                binding.play.isChecked = false
            }
            binding.audioBar.setOnSeekBarChangeListener(object :
                SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    if (fromUser) mediaPlayer?.seekTo(progress)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            })
            binding.removeAudio.setOnClickListener {
                findNavController().navigateUp()
                mediaPlayer?.release()
                mediaPlayer = null
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