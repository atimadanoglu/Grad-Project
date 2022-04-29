package com.graduationproject.grad_project.view.resident

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.graduationproject.grad_project.adapter.MessagesListRecyclerViewAdapter
import com.graduationproject.grad_project.databinding.FragmentIncomingMessagesResidentBinding
import com.graduationproject.grad_project.viewmodel.IncomingMessagesResidentViewModel

class IncomingMessagesResidentFragment : Fragment() {

    private var _binding: FragmentIncomingMessagesResidentBinding? = null
    private val binding get() = _binding!!
    private val viewModel: IncomingMessagesResidentViewModel by viewModels()
    private var recyclerViewAdapter: MessagesListRecyclerViewAdapter? = null

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
        recyclerViewAdapter = MessagesListRecyclerViewAdapter(requireContext(), parentFragmentManager)
        binding.messageRecyclerview.adapter = recyclerViewAdapter
        binding.viewModel = viewModel
        return view
    }
}
