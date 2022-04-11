package com.graduationproject.grad_project

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.graduationproject.grad_project.adapter.RequestsListRecyclerViewAdapter
import com.graduationproject.grad_project.databinding.FragmentRequestBinding
import com.graduationproject.grad_project.viewmodel.RequestsViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RequestFragment(
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val mainDispatcher: CoroutineDispatcher = Dispatchers.Main
) : Fragment() {

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
        listItems()
        viewModel.requests.observe(viewLifecycleOwner) {

        }
        binding.floatingActionButton.setOnClickListener {
            openAddRequestDialogFragment()
        }
        binding.requestsRecyclerview.layoutManager = LinearLayoutManager(this.context)
        requestsAdapter = viewModel.requests.value?.let { requests ->
            context?.let { context ->
                RequestsListRecyclerViewAdapter(
                    requests,
                    context
                )
            }
        }
        binding.requestsRecyclerview.adapter = requestsAdapter
        return view
    }

    private fun openAddRequestDialogFragment() {
        TODO("Not yet implemented")
    }

    private fun listItems() {
        lifecycleScope.launch(ioDispatcher) {
            val email = FirebaseAuth.getInstance().currentUser?.email.toString()
            viewModel.retrieveRequests(email)
        }
    }
}