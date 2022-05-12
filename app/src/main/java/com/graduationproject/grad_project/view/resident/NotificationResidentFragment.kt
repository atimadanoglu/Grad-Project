package com.graduationproject.grad_project.view.resident

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.graduationproject.grad_project.R
import com.graduationproject.grad_project.adapter.NotificationsRecyclerViewAdapter
import com.graduationproject.grad_project.databinding.FragmentNotificationResidentBinding
import com.graduationproject.grad_project.viewmodel.NotificationsResidentViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class NotificationResidentFragment : Fragment() {

    private var _binding: FragmentNotificationResidentBinding? = null
    private val binding get() = _binding!!
    private var notificationsRecyclerViewAdapter: NotificationsRecyclerViewAdapter? = null
    private val viewModel: NotificationsResidentViewModel by viewModels()
    private var menuView: View? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentNotificationResidentBinding.inflate(inflater, container, false)
        val view = binding.root
        notificationsRecyclerViewAdapter = NotificationsRecyclerViewAdapter { notification, menu ->
            menuView = menu
            viewModel.saveInfo(notification)
        }
        binding.notificationRecyclerview.adapter = notificationsRecyclerViewAdapter
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        viewModel.notifications.observe(viewLifecycleOwner) {
            notificationsRecyclerViewAdapter?.submitList(it)
        }
        viewModel.openMenuOptions.observe(viewLifecycleOwner) {
            it?.let {
                if (it) {
                    chooseAction()
                }
            }
        }
        binding.deleteNotificationButton.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                binding.notificationRecyclerview.alpha = 0F
                viewModel.clearNotifications()
                delay(4000L)
                binding.notificationRecyclerview.alpha = 1F
            }
        }
        return view
    }

    private fun goToShowExpenditureFragment(
        downloadUri: String, title: String, content: String
    ) {
        val action = NotificationResidentFragmentDirections
            .actionNotificationResidentFragmentToShowNotificationDialogFragment(downloadUri, title, content)
        findNavController().navigate(action)
    }

    private fun createPopUpMenu(): PopupMenu? {
        val popupMenu = menuView?.let { PopupMenu(requireContext(), it) }
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
                    goToShowExpenditureFragment(
                        viewModel.notification.value?.pictureUri!!,
                        viewModel.notification.value?.title!!,
                        viewModel.notification.value?.message!!
                    )
                    true
                }
                R.id.deleteExpenditure -> {
                    viewModel.deleteNotification()
                    true
                }
                else -> false
            }
        }
    }
}
