package com.netology.nmedia.activity

import android.app.Activity
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import com.github.dhaval2404.imagepicker.constant.ImageProvider
import com.google.android.material.snackbar.Snackbar
import com.netology.nmedia.R
import com.netology.nmedia.activity.VideoFragment.Companion.videoArg
import com.netology.nmedia.databinding.FragmentNewEventBinding
import com.netology.nmedia.enums.AttachmentType
import com.netology.nmedia.util.AndroidUtils
import com.netology.nmedia.util.Formatter
import com.netology.nmedia.viewmodel.EventViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class NewEventFragment : Fragment() {
    private val viewModel: EventViewModel by activityViewModels()

    companion object {
        private var mediaPlayer: MediaPlayer? = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentNewEventBinding.inflate(inflater, container, false)
        val draftContent = viewModel.getDraftContent()
        val draftLink = viewModel.getDraftLink()
        val draftDate = viewModel.getDraftDate()
        val draftTime = viewModel.getDraftTime()
        val draftFormat = viewModel.getDraftFormat()
        var type: AttachmentType? = null
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
                        val stream = uri?.let { stream ->
                            context?.contentResolver?.openInputStream(stream)
                        }

                        viewModel.changeMedia(uri, stream, type)
                    }
                }
            }
        val mediaContract =
            registerForActivityResult(ActivityResultContracts.GetContent()) {
                it?.let {
                    val stream = context?.contentResolver?.openInputStream(it)
                    viewModel.changeMedia(it, stream, type)
                }
            }
        activity?.title = getString(R.string.new_event)
        binding.edit.setText(draftContent)
        binding.date.text = draftDate
        binding.time.text = draftTime

        binding.onlineOrOffline.text = draftFormat

        binding.onlineOrOffline.setOnCheckedChangeListener { button, _ ->
            if (button.isChecked) {
                binding.onlineOrOffline.text = getString(R.string.Online)
            } else {
                binding.onlineOrOffline.text = getString(R.string.Offline)
            }
        }


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
        binding.date.setOnClickListener {
            context?.let { Formatter.showDatePicker(binding.date, it) }
        }
        binding.time.setOnClickListener {
            context?.let { Formatter.showTimePicker(binding.time, it) }
        }
        activity?.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.new_event_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.save -> {
                        val date = "${binding.date.text} " + "${binding.time.text}"
                        if (binding.edit.text.isNotBlank() && binding.date.text.isNotBlank() && binding.time.text.isNotBlank()) {
                            viewModel.changeContent(
                                binding.edit.text.toString(),
                                binding.linkText.text.toString(),
                                date,
                                binding.onlineOrOffline.isChecked
                            )
                            viewModel.save()
                            viewModel.clearDrafts()
                            AndroidUtils.hideKeyboard(requireView())
                            true
                        } else {
                            Snackbar.make(
                                binding.root,
                                getString(R.string.error_empty_field), Snackbar.LENGTH_LONG
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
            type = AttachmentType.IMAGE
            binding.audioAttachment.isVisible = false
            binding.videoAttachment.isVisible = false
            binding.deleteVideo.isVisible = false
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
            type = AttachmentType.IMAGE
            binding.audioAttachment.isVisible = false
            binding.videoAttachment.isVisible = false
            binding.deleteVideo.isVisible = false
        }
        binding.addAudio.setOnClickListener {
            mediaContract.launch("audio/*")
            type = AttachmentType.AUDIO
            binding.videoAttachment.isVisible = false
            binding.deleteVideo.isVisible = false
            binding.photoPreviewContainer.isVisible = false
        }
        binding.addVideo.setOnClickListener {
            mediaContract.launch("video/*")
            type = AttachmentType.VIDEO
            binding.photoPreviewContainer.isVisible = false
            binding.audioAttachment.isVisible = false
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            viewModel.saveDraftContent(binding.edit.text.toString())
            viewModel.saveDraftDate(binding.date.text.toString())
            viewModel.saveDraftTime(binding.time.text.toString())
            val format = if (binding.onlineOrOffline.isChecked) {
                getString(R.string.Online)
            } else {
                getString(R.string.Offline)
            }
            viewModel.saveDraftFormat(format)
            viewModel.saveDraftLink(binding.linkText.text.toString())
            AndroidUtils.hideKeyboard(requireView())
            findNavController().navigateUp()
        }
        binding.removePhoto.setOnClickListener {
            viewModel.changeMedia(null, null, null)
        }
        binding.deleteVideo.setOnClickListener {
            binding.videoAttachment.isVisible = false
            binding.deleteVideo.isVisible = false
            viewModel.changeMedia(null, null, null)
            viewModel.changeAttachmentVideo("")
        }
        viewModel.mediaState.observe(viewLifecycleOwner) { mediaState ->
            if (mediaState == null) {
                binding.photoPreviewContainer.visibility = View.GONE
                return@observe
            }
            if (mediaState.type == AttachmentType.IMAGE) {
                binding.photoPreviewContainer.isVisible = true
                binding.photoPreview.setImageURI(mediaState.uri)
            } else {
                binding.photoPreviewContainer.isVisible = false
            }
            if (mediaState.type == AttachmentType.AUDIO) {

                binding.play.setOnClickListener {
                    if (mediaPlayer == null) {
                        mediaPlayer = MediaPlayer.create(context, mediaState.uri)
                        binding.audioBar.max = mediaPlayer!!.duration
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
                    binding.audioAttachment.isVisible = false
                    viewModel.changeMedia(null, null, null)
                    mediaPlayer?.release()
                    mediaPlayer = null
                }
                binding.audioAttachment.isVisible = true
            }
            if (mediaState.type == AttachmentType.VIDEO) {
                binding.videoAttachment.isVisible = true
                binding.deleteVideo.isVisible = true
                binding.playVideo.setOnClickListener {
                    findNavController().navigate(
                        R.id.action_newEventFragment_to_videoFragment,
                        Bundle().apply {
                            videoArg = mediaState.uri.toString()
                        })
                    AndroidUtils.hideKeyboard(requireView())
                }
                binding.videoAttachmentHeader.setOnClickListener {
                    findNavController().navigate(
                        R.id.action_newEventFragment_to_videoFragment,
                        Bundle().apply {
                            videoArg = mediaState.uri.toString()
                        })
                    AndroidUtils.hideKeyboard(requireView())
                }

            }
        }
        viewModel.eventCreated.observe(viewLifecycleOwner) {
            findNavController().navigateUp()
        }
        return binding.root
    }

    override fun onDestroy() {
        viewModel.clear()
        viewModel.changeMedia(null, null, null)
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
