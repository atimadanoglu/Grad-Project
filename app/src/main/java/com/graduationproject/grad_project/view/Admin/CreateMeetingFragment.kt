package com.graduationproject.grad_project.view.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.graduationproject.grad_project.databinding.FragmentCreateMeetingBinding
import com.graduationproject.grad_project.viewmodel.CreateMeetingViewModel

class CreateMeetingFragment : Fragment() {

    private var _binding: FragmentCreateMeetingBinding? = null
    private val binding get() = _binding!!
   private val viewModel: CreateMeetingViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentCreateMeetingBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.timePicker.setIs24HourView(true)
        setTime()
        checkTitleIsEmpty()
        viewModel.retrieveResidents()
        viewModel.residents.observe(viewLifecycleOwner) {
            it?.let {
                if (it.isNotEmpty()) {
                    viewModel.setPlayerIdsAndEmails()
                }
            }
        }
        viewModel.navigateToMeetingsFragment.observe(viewLifecycleOwner) {
            it?.let {
                if (it) {
                    val action = CreateMeetingFragmentDirections
                        .actionCreateMeetingFragmentToMeetingAdminFragment()
                    findNavController().navigate(action)
                }
            }
        }
        return binding.root
    }

    private fun checkTitleIsEmpty() {
        binding.crateMeetingButton.isEnabled = false
        binding.titleMeetingEditText.addTextChangedListener {
            if (it.isNullOrEmpty()) {
                binding.crateMeetingButton.isEnabled = false
                binding.titleMeetingInputText.error = " "
            } else {
                binding.crateMeetingButton.isEnabled = true
                binding.titleMeetingInputText.error = null
            }
        }
    }

    private fun setTime() {
        binding.timePicker.setOnTimeChangedListener { _, hour, minute ->
            viewModel.setHour(hour.toLong())
            viewModel.setMinute(minute.toLong())
        }
    }
}