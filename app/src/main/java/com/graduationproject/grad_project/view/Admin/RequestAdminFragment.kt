package com.graduationproject.grad_project.view.admin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.graduationproject.grad_project.R
import com.graduationproject.grad_project.adapter.AdminRequestsListAdapter
import com.graduationproject.grad_project.databinding.FragmentRequestAdminBinding
import com.graduationproject.grad_project.viewmodel.AdminRequestsListViewModel

class RequestAdminFragment : Fragment() {

    private var _binding: FragmentRequestAdminBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: AdminRequestsListAdapter
    private val viewModel: AdminRequestsListViewModel by viewModels()
    private var popupMenu: View? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentRequestAdminBinding.inflate(inflater, container, false)
        adapter = AdminRequestsListAdapter { request, view ->
            popupMenu = view
            viewModel.saveInfo(request)
        }
        binding.requestsRecyclerview.adapter = adapter
        viewModel.retrieveRequests()
        viewModel.requests.observe(viewLifecycleOwner) {
            it?.let {
                adapter.submitList(it)
            }
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

    private fun goToShowRequestFragment(
        downloadUri: String, title: String, content: String, type: String
    ) {
        val action = RequestAdminFragmentDirections
            .actionRequestAdminFragmentToShowRequestInfoDialogFragment(
                downloadUri, title, content, type
            )
        findNavController().navigate(action)
    }

    private fun createPopUpMenu(): PopupMenu? {
        val menu = popupMenu?.let { PopupMenu(requireContext(), it) }
        val inflater = menu?.menuInflater
        inflater?.inflate(R.menu.request_menu, menu.menu)
        menu?.show()
        return menu
    }

    private fun chooseAction() {
        val menu = createPopUpMenu()
        menu?.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.requestInfo -> {
                    goToShowRequestFragment(
                        viewModel.request.value?.documentUri!!,
                        viewModel.request.value?.title!!,
                        viewModel.request.value?.content!!,
                        viewModel.request.value?.type!!
                    )
                    true
                }
                R.id.deleteRequest -> {
                    viewModel.deleteRequest()
                    true
                }
                else -> false
            }
        }
    }

}