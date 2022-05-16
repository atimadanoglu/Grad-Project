package com.graduationproject.grad_project.view.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.graduationproject.grad_project.R
import com.graduationproject.grad_project.adapter.NotificationsAdminAdapter
import com.graduationproject.grad_project.databinding.FragmentNotificationsAdminBinding
import com.graduationproject.grad_project.viewmodel.NotificationsAdminViewModel

class NotificationsAdminFragment : Fragment() {

    private var _binding: FragmentNotificationsAdminBinding? = null
    private val binding get() = _binding!!
    private val viewModel: NotificationsAdminViewModel by viewModels()
    private lateinit var adapter: NotificationsAdminAdapter
    private var menuView: View? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentNotificationsAdminBinding.inflate(inflater, container, false)
        adapter = NotificationsAdminAdapter { notification, view ->
            if (view != null) {
                menuView = view
            }
            viewModel.saveInfo(notification)
        }
        viewModel.notifications.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
        viewModel.retrieveNotifications()
        binding.notificationRecyclerview.adapter = adapter
        binding.lifecycleOwner = viewLifecycleOwner
        binding.deleteNotificationButton.setOnClickListener {
            areYouSureYouWantToDeleteAllNotifications()
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

    private fun createPopUpMenu(): androidx.appcompat.widget.PopupMenu? {
        val popupMenu = menuView?.let { androidx.appcompat.widget.PopupMenu(requireContext(), it) }
        val inflater = popupMenu?.menuInflater
        inflater?.inflate(R.menu.announcement_more_menu, popupMenu.menu)
        popupMenu?.show()
        return popupMenu
    }

    private fun areYouSureYouWantToDeleteAllNotifications() {
        MaterialAlertDialogBuilder(requireContext())
            .setMessage("Bütün bildirimleri silmek istediğinize emin misiniz?")
            .setPositiveButton(R.string.evet) { _, _ ->
                viewModel.deleteAllNotifications()
            }.setNegativeButton(R.string.hayır) { _, _ -> }
            .create().show()
    }

    private fun chooseAction() {
        val menu = createPopUpMenu()
        menu?.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.announcementInfo -> {
                    goToShowNotificationFragment(
                        viewModel.notification.value?.pictureUri!!,
                        viewModel.notification.value?.title!!,
                        viewModel.notification.value?.message!!
                    )
                    true
                }
                R.id.deleteAnnouncement -> {
                    viewModel.deleteNotification()
                    true
                }
                else -> false
            }
        }
    }

    private fun goToShowNotificationFragment(
        downloadUri: String, title: String, content: String
    ) {
        val action = NotificationsAdminFragmentDirections
            .actionNotificationsAdminFragmentToShowNotificationDialogFragment2(
                downloadUri, title, content
            )
        findNavController().navigate(action)
    }

}