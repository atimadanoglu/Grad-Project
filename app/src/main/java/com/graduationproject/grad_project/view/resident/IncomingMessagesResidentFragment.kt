package com.graduationproject.grad_project.view.resident

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.graduationproject.grad_project.R
import com.graduationproject.grad_project.adapter.MessagesListRecyclerViewAdapter
import com.graduationproject.grad_project.databinding.FragmentIncomingMessagesResidentBinding
import com.graduationproject.grad_project.firebase.MessagesOperations
import com.graduationproject.grad_project.view.resident.dialogs.ShowMessageDialogFragment
import com.graduationproject.grad_project.viewmodel.IncomingMessagesResidentViewModel

class IncomingMessagesResidentFragment : Fragment() {

    private var _binding: FragmentIncomingMessagesResidentBinding? = null
    private val binding get() = _binding!!
    private val viewModel: IncomingMessagesResidentViewModel by viewModels()
    private var recyclerViewAdapter: MessagesListRecyclerViewAdapter? = null
    private lateinit var anchorView: View
    private lateinit var popupMenu: PopupMenu

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentIncomingMessagesResidentBinding.inflate(inflater,container, false)
        val view = binding.root
        viewModel.messages.observe(viewLifecycleOwner) {
            recyclerViewAdapter?.submitList(it)
        }
        viewModel.retrieveMessages()
        recyclerViewAdapter = MessagesListRecyclerViewAdapter { message, anchor ->
            viewModel.saveMessage(message)
            anchorView = anchor
            createPopUpMenu()
            popUpMenuItemClickListener()
        }
        binding.messageRecyclerview.adapter = recyclerViewAdapter
        binding.viewModel = viewModel
        return view
    }

    private fun createPopUpMenu() {
        popupMenu = PopupMenu(requireContext(), anchorView)
        val inflater = popupMenu.menuInflater
        inflater.inflate(R.menu.announcement_more_menu, popupMenu.menu)
        popupMenu.show()
    }

    private fun popUpMenuItemClickListener() {
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.announcementInfo -> {
                    val showMessageDialogFragment = viewModel.message.value?.let { ShowMessageDialogFragment(it) }
                    showMessageDialogFragment?.show(parentFragmentManager, "showMessageDialog")
                    true
                }
                R.id.deleteAnnouncement -> {
                    viewModel.message.value?.let { MessagesOperations.deleteMessageInSpecificPosition(it) }
                    true
                }
                else -> false
            }
        }
    }
}
