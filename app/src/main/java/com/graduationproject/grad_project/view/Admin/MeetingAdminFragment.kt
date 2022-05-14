package com.graduationproject.grad_project.view.admin

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.graduationproject.grad_project.adapter.MeetingAdminAdapter
import com.graduationproject.grad_project.databinding.FragmentMeetingAdminBinding
import com.graduationproject.grad_project.viewmodel.MeetingAdminViewModel

class MeetingAdminFragment : Fragment() {

    private var _binding: FragmentMeetingAdminBinding?  =null
    private val binding get() = _binding!!
    private val viewModel: MeetingAdminViewModel by viewModels()
    private lateinit var adapter: MeetingAdminAdapter
    private var viewHolderImageView: View? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentMeetingAdminBinding.inflate(inflater, container, false)
        checkShareLinkButton()
        adapter = MeetingAdminAdapter { id, view ->
            viewHolderImageView = view
            viewModel.saveViewHolderData(id)
            goToGoogleMeetApp()
        }
        binding.viewModel = viewModel
        binding.meetingRecyclerView.adapter = adapter
        binding.lifecycleOwner = viewLifecycleOwner
        viewModel.retrieveResidents()
        viewModel.retrieveMeetings()
        createNotificationChannel()
        viewModel.residents.observe(viewLifecycleOwner) {
            it?.let {
                if (it.isNotEmpty()) {
                    viewModel.setPlayerIdsAndEmails()
                }
            }
        }
        binding.scheduleMeetingButton.setOnClickListener {
            val action = MeetingAdminFragmentDirections
                .actionMeetingAdminFragmentToCreateMeetingFragment()
            findNavController().navigate(action)
        }
        viewModel.meetings.observe(viewLifecycleOwner) {
            it?.let {
                it[0]?.id?.let { it1 -> viewModel.setMeetingID(it1) }
                adapter.submitList(it)
            }
        }
        viewModel.isLinkShared.observe(viewLifecycleOwner) {
            Snackbar.make(
                requireView(),
                "Toplantı linki sakinlerle paylaşıldı!",
                Snackbar.LENGTH_LONG
            ).show()
        }
        return binding.root
    }

    private fun checkShareLinkButton() {
        binding.shareLinkButton.isEnabled = false
        binding.linkEditText.addTextChangedListener {
            binding.shareLinkButton.isEnabled = binding.linkEditText.text?.isNotEmpty() == true
        }
    }

    private fun goToGoogleMeetApp() {
        val googleMeetIntent = Intent(Intent.ACTION_VIEW,
            Uri.parse("https://meet.google.com/")
        )
        startActivity(googleMeetIntent)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "adminReminderChannel"
            val description = "Channel for Alarm Manager"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("adminChannel", name, importance)
            channel.description = description
            val notificationManager = activity?.getSystemService(
                NotificationManager::class.java
            )
            notificationManager?.createNotificationChannel(channel)
        }
    }
}