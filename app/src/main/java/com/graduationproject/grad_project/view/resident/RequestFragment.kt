package com.graduationproject.grad_project.view.resident

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.graduationproject.grad_project.adapter.RequestsListRecyclerViewAdapter
import com.graduationproject.grad_project.databinding.FragmentRequestBinding
import com.graduationproject.grad_project.viewmodel.RequestsViewModel

class RequestFragment: Fragment() {

    private var _binding: FragmentRequestBinding? = null
    private val binding get() = _binding!!
    private lateinit var requestsAdapter: RequestsListRecyclerViewAdapter
    private val viewModel: RequestsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentRequestBinding.inflate(inflater, container, false)
        val view = binding.root
        requestsAdapter = RequestsListRecyclerViewAdapter(parentFragmentManager, requireContext())
        binding.requestsRecyclerview.adapter = requestsAdapter
        binding.lifecycleOwner = viewLifecycleOwner
        viewModel.retrieveRequests()
        viewModel.requests.observe(viewLifecycleOwner) {
            requestsAdapter.submitList(it)
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
}