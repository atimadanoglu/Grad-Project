package com.graduationproject.grad_project

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.graduationproject.grad_project.adapter.RequestsListRecyclerViewAdapter
import com.graduationproject.grad_project.databinding.FragmentRequestBinding
import com.graduationproject.grad_project.viewmodel.RequestsViewModel
import kotlinx.coroutines.*

class RequestFragment: Fragment() {

    private var _binding: FragmentRequestBinding? = null
    private val binding get() = _binding!!
    private var requestsAdapter: RequestsListRecyclerViewAdapter? = null
    private val viewModel: RequestsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentRequestBinding.inflate(inflater, container, false)
        val view = binding.root
        lifecycleScope.launch {
            viewModel.retrieveRequests()
            binding.requestsRecyclerview.layoutManager = LinearLayoutManager(this@RequestFragment.context)
            requestsAdapter =
                context?.let { context ->
                    viewModel.requests.value?.let { requests ->
                    RequestsListRecyclerViewAdapter(
                        requests, parentFragmentManager, context)
                } }
            binding.requestsRecyclerview.adapter = requestsAdapter
            requestsAdapter?.submitList(viewModel.requests.value)
        }

        viewModel.requests.observe(viewLifecycleOwner) {
            requestsAdapter?.submitList(it)
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