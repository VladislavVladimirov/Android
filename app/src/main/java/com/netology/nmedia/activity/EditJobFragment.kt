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
import com.netology.nmedia.databinding.FragmentEditJobBinding
import com.netology.nmedia.util.AndroidUtils
import com.netology.nmedia.util.Formatter
import com.netology.nmedia.viewmodel.WallViewModel


class EditJobFragment : Fragment() {
    private val wallViewModel: WallViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentEditJobBinding.inflate(inflater, container, false)

        val editedJob = wallViewModel.getEditedJob()

        activity?.title = getString(R.string.edit_job)

        binding.apply {
            companyName.setText(editedJob?.name)
            position.setText(editedJob?.position)
            startWork.text = editedJob?.start?.let { Formatter.formatJobDateForEdit(it) }
            finishWork.text = editedJob?.finish?.let { Formatter.formatJobDateForEdit(it) }
            link.setText(editedJob?.link)

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
                    menuInflater.inflate(R.menu.edit_job_menu, menu)
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

                        R.id.cancel -> {
                            AndroidUtils.hideKeyboard(requireView())
                            findNavController().navigateUp()
                        }

                        else -> {
                            wallViewModel.clear()
                            false
                        }
                    }
                }
            }, viewLifecycleOwner)
            requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
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