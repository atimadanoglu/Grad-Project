package com.graduationproject.grad_project.view.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.graduationproject.grad_project.R
import com.graduationproject.grad_project.adapter.AnnouncementRecyclerViewAdapter
import com.graduationproject.grad_project.databinding.FragmentAnnouncementsBinding
import com.graduationproject.grad_project.viewmodel.AnnouncementsViewModel

class AnnouncementsFragment : Fragment() {

    private var _binding : FragmentAnnouncementsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AnnouncementsViewModel by viewModels()
    private var menuView: View? = null
    private lateinit var announcementRecyclerViewAdapter: AnnouncementRecyclerViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentAnnouncementsBinding.inflate(inflater, container, false)
        announcementRecyclerViewAdapter = AnnouncementRecyclerViewAdapter { announcement, view ->
            menuView = view
            viewModel.saveInfo(announcement)
        }
        binding.announcementRecyclerview.adapter = announcementRecyclerViewAdapter
        binding.lifecycleOwner = viewLifecycleOwner
        viewModel.announcements.observe(viewLifecycleOwner) {
            announcementRecyclerViewAdapter.submitList(it)
        }
        viewModel.retrieveAnnouncements()
        binding.addAnnouncementButton.setOnClickListener {
            val action = AnnouncementsFragmentDirections.actionAnnouncementsFragmentToAddAnnouncementFragment()
            requireView().findNavController().navigate(action)
        }
        viewModel.openMenuOptions.observe(viewLifecycleOwner) {
            it?.let {
                if (it) {
                    chooseAction()
                }
            }
        }
        return binding.root
    }

    private fun goToShowAnnouncementFragment(
        downloadUri: String, title: String, content: String
    ) {
        val action = AnnouncementsFragmentDirections
            .actionAnnouncementsFragmentToShowNotificationDialogFragment2(downloadUri, title, content)
        findNavController().navigate(action)
    }

    private fun createPopUpMenu(): androidx.appcompat.widget.PopupMenu? {
        val popupMenu = menuView?.let { androidx.appcompat.widget.PopupMenu(requireContext(), it) }
        val inflater = popupMenu?.menuInflater
        inflater?.inflate(R.menu.expenditures_menu, popupMenu.menu)
        popupMenu?.show()
        return popupMenu
    }

    private fun chooseAction() {
        val menu = createPopUpMenu()
        menu?.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.showExpenditure -> {
                    goToShowAnnouncementFragment(
                        viewModel.announcement.value?.pictureUri!!,
                        viewModel.announcement.value?.title!!,
                        viewModel.announcement.value?.pictureUri!!
                    )
                    true
                }
                R.id.deleteExpenditure -> {
                    viewModel.deleteAnnouncement()
                    true
                }
                else -> false
            }
        }
    }
}