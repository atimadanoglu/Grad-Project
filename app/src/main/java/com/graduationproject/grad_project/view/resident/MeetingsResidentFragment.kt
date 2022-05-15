package com.graduationproject.grad_project.view.resident

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.graduationproject.grad_project.databinding.FragmentMeetingsResidentBinding
import com.graduationproject.grad_project.viewmodel.MeetingsResidentsViewModel

class MeetingsResidentFragment : Fragment() {

    private var _binding: FragmentMeetingsResidentBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MeetingsResidentsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentMeetingsResidentBinding.inflate(inflater, container, false)
        binding.linkResidentCardView.visibility = View.GONE
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        viewModel.retrieveMeeting()
        binding.joinMeetingImage.setOnClickListener {
            goToGoogleMeetApp()
        }
        viewModel.meetingInfo.observe(viewLifecycleOwner) {
            it?.let {
                viewModel.checkLinkValidity(it.meetingUri)
                if (it.meetingUri.isNotEmpty() && viewModel.isValidUri.value == true) {
                    binding.linkResidentCardView.visibility = View.VISIBLE
                    binding.noMeetingText.visibility = View.GONE
                } else {
                    binding.linkResidentCardView.visibility = View.GONE
                    binding.noMeetingText.visibility = View.VISIBLE
                }
            }
        }
        return binding.root
    }

    private fun goToGoogleMeetApp() {
        val googleMeetIntent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("${viewModel.meetingInfo.value?.meetingUri}")
        )
        startActivity(googleMeetIntent)
    }

}