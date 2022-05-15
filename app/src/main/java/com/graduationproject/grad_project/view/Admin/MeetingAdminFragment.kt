package com.graduationproject.grad_project.view.admin

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
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
import com.graduationproject.grad_project.service.AlarmReceiver
import com.graduationproject.grad_project.viewmodel.MeetingAdminViewModel
import java.util.*

class MeetingAdminFragment : Fragment() {

    private var _binding: FragmentMeetingAdminBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MeetingAdminViewModel by viewModels()
    private lateinit var adapter: MeetingAdminAdapter
    private var viewHolderImageView: View? = null
    private lateinit var alarmManager: AlarmManager
    private lateinit var pendingIntent: PendingIntent
    private val calendar = Calendar.getInstance()
    private val currentTime = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentMeetingAdminBinding.inflate(inflater, container, false)
        checkShareLinkButton()
        adapter = MeetingAdminAdapter { meeting, view ->
            viewHolderImageView = view
            viewModel.saveViewHolderData(meeting)
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
                adapter.submitList(it)
                it[0]?.id?.let { it1 -> viewModel.setMeetingID(it1) }
                it[0]?.let { meeting ->
                    if (meeting.minute.toInt() > 10) {
                        calendar[Calendar.HOUR_OF_DAY] = meeting.hour.toInt()
                        calendar[Calendar.MINUTE] = meeting.minute.toInt() - 10
                        calendar[Calendar.SECOND] = 0
                        calendar[Calendar.MILLISECOND] = 0
                    } else if (meeting.minute.toInt() == 0) {
                        calendar[Calendar.HOUR_OF_DAY] = meeting.hour.toInt() - 1
                        calendar[Calendar.MINUTE] = 50
                        calendar[Calendar.SECOND] = 0
                        calendar[Calendar.MILLISECOND] = 0
                    } else if (meeting.minute.toInt() in 1..10) {
                        calendar[Calendar.HOUR_OF_DAY] = meeting.hour.toInt() - 1
                        calendar[Calendar.MINUTE] = 60 - (10 - meeting.minute.toInt())
                        calendar[Calendar.SECOND] = 0
                        calendar[Calendar.MILLISECOND] = 0
                    }
                    viewModel.setMeeting(meeting)
                    setAlarm()
                }
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

    private fun setAlarm() {
        alarmManager = activity?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(requireActivity(), AlarmReceiver::class.java)
        pendingIntent = PendingIntent.getBroadcast(requireContext(), 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP, calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY, pendingIntent
        )
        viewModel.meeting.value?.let {
            if (currentTime[Calendar.HOUR_OF_DAY] >= it.hour &&
                    currentTime[Calendar.MINUTE] >= it.minute) {
                alarmManager.cancel(pendingIntent)
            }
        }
    }

    private fun checkShareLinkButton() {
        binding.shareLinkButton.isEnabled = false
        binding.linkEditText.addTextChangedListener {
            viewModel.checkLinkValidity(it.toString())
            binding.shareLinkButton.isEnabled =
                binding.linkEditText.text?.isNotEmpty() == true && viewModel.isValidUri.value == true
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