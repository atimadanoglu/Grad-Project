package com.graduationproject.grad_project.view.resident

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.graduationproject.grad_project.R
import com.graduationproject.grad_project.adapter.RequestsListRecyclerViewAdapter
import com.graduationproject.grad_project.databinding.FragmentRequestBinding
import com.graduationproject.grad_project.firebase.RequestsOperations
import com.graduationproject.grad_project.view.resident.dialogs.ShowRequestInfoResidentDialogFragment
import com.graduationproject.grad_project.viewmodel.RequestsViewModel

class RequestFragment: Fragment() {

    private var _binding: FragmentRequestBinding? = null
    private val binding get() = _binding!!
    private lateinit var requestsAdapter: RequestsListRecyclerViewAdapter
    private val viewModel: RequestsViewModel by viewModels()
    private lateinit var popupMenu: PopupMenu
    // Anchor view for popupMenu
    private lateinit var anchorView: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentRequestBinding.inflate(inflater, container, false)
        val view = binding.root
        requestsAdapter = RequestsListRecyclerViewAdapter { request, anchor ->
            viewModel.saveClickedRequest(request)
            anchorView = anchor
            createPopUpMenu()
            popUpMenuItemClickListener()
        }
        binding.requestsRecyclerview.adapter = requestsAdapter
        binding.lifecycleOwner = viewLifecycleOwner
        viewModel.retrieveRequests()
        viewModel.requests.observe(viewLifecycleOwner) { requests ->
            requestsAdapter.submitList(requests)
        }
        binding.floatingActionButton.setOnClickListener {
            openAddRequestDialogFragment()
        }
        return view
    }

    private fun openAddRequestDialogFragment() {
        val action = RequestFragmentDirections.actionRequestFragmentToAddRequestFragment()
        findNavController().navigate(action)
    }

    private fun createPopUpMenu() {
        popupMenu = PopupMenu(requireContext(), anchorView)
        val inflater = popupMenu.menuInflater
        inflater.inflate(R.menu.request_menu, popupMenu.menu)
        popupMenu.show()
    }

    private fun popUpMenuItemClickListener() {
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when(menuItem.itemId) {
                R.id.requestInfo -> {
                    val showRequest = viewModel.request.value?.let {
                        ShowRequestInfoResidentDialogFragment(it)
                    }
                    showRequest?.show(parentFragmentManager, "showRequestDialog")
                    true
                }
                R.id.deleteRequest -> {
                    viewModel.request.value?.let { RequestsOperations.deleteRequestFromResidentDB(it) }
                    true
                }
                else -> false
            }
        }
    }
}