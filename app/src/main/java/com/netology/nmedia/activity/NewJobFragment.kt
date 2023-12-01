package com.netology.nmedia.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.netology.nmedia.R
import com.netology.nmedia.databinding.FragmentNewJobBinding
import com.netology.nmedia.util.AndroidUtils
import com.netology.nmedia.util.Formatter
import com.netology.nmedia.viewmodel.WallViewModel


class NewJobFragment : Fragment() {
    private val wallViewModel: WallViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentNewJobBinding.inflate(inflater, container, false)
        val draftName = wallViewModel.getDraftName()
        val draftPosition = wallViewModel.getDraftPosition()
        val draftStart = wallViewModel.getDraftStart()
        val draftFinish = wallViewModel.getDraftFinish()
        val draftLink = wallViewModel.getDraftLink()

        activity?.title = getString(R.string.add_job_title)

        binding.apply {
            companyName.setText(draftName)
            position.setText(
                draftPosition
            )
            startWork.text = draftStart
            finishWork.text = draftFinish
            link.setText(draftLink)
            if (link.text.isNotBlank()) {
                link.visibility = View.VISIBLE
                deleteLink.visibility = View.VISIBLE
            }
            addLink.setOnClickListener {
                link.visibility = View.VISIBLE
                deleteLink.visibility = View.VISIBLE
            }
            deleteLink.setOnClickListener {
                link.visibility = View.GONE
                link.text.clear()
                deleteLink.visibility = View.GONE
            }
            startWork.setOnClickListener {
                context?.let { Formatter.showDatePicker(startWork, it) }
            }
            finishWork.setOnClickListener {
                context?.let { Formatter.showDatePicker(finishWork, it) }
            }
            activity?.addMenuProvider(object : MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.new_job_menu, menu)
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                    return when (menuItem.itemId) {
                        R.id.save -> {
                            if (companyName.text.isNotBlank() && position.text.isNotBlank() && startWork.text.isNotBlank() && finishWork.text.isNotBlank()) {
                                wallViewModel.changeContent(
                                    companyName.text.toString(),
                                    position.text.toString(),
                                    startWork.text.toString(),
                                    finishWork.text.toString(),
                                    link.text.toString()
                                )
                                wallViewModel.saveJob()
                                wallViewModel.clearDrafts()
                                AndroidUtils.hideKeyboard(requireView())
                                true
                            } else {
                                Snackbar.make(
                                    root,
                                    getString(R.string.error_empty_field),
                                    Snackbar.LENGTH_LONG
                                ).show()
                                false
                            }
                        }

                        else -> {
                            wallViewModel.clear()
                            false
                        }
                    }
                }
            }, viewLifecycleOwner)
            requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
                wallViewModel.saveDraftName(binding.companyName.text.toString())
                wallViewModel.saveDraftPosition(binding.position.text.toString())
                wallViewModel.saveDraftStart(binding.startWork.text.toString())
                wallViewModel.saveDraftFinish(binding.finishWork.text.toString())
                wallViewModel.saveDraftLink(binding.link.text.toString())
                AndroidUtils.hideKeyboard(requireView())
                findNavController().navigateUp()
            }
        }
        wallViewModel.jobCreated.observe(viewLifecycleOwner) {
            findNavController().navigateUp()
        }
        return binding.root
    }
}