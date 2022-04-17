package com.graduationproject.grad_project.view.resident

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.graduationproject.grad_project.adapter.MessagesListRecyclerViewAdapter
import com.graduationproject.grad_project.databinding.FragmentIncomingMessagesResidentBinding
import com.graduationproject.grad_project.model.Message
import com.graduationproject.grad_project.viewmodel.IncomingMessagesResidentViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class IncomingMessagesResidentFragment(
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : Fragment() {

    private var _binding: FragmentIncomingMessagesResidentBinding? = null
    private val binding get() = _binding!!
    private val viewModel: IncomingMessagesResidentViewModel by viewModels()
    private var recyclerViewAdapter: MessagesListRecyclerViewAdapter? = null
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentIncomingMessagesResidentBinding.inflate(inflater,container, false)
        val view = binding.root
        auth = FirebaseAuth.getInstance()
        binding.deleteMessageButton.setOnClickListener {
            lifecycleScope.launch {
                val email = auth.currentUser?.email
                if (email != null) {
                    deleteMessageButtonClicked(email)
                }
            }
        }
        observeLiveData()
        auth.currentUser?.email?.let { email ->
            viewModel.retrieveMessages(email)
        }
        viewModel.messages.value?.let { value ->
            adaptThisFragmentWithRecyclerView(value)
        }
        viewModel.messages.value?.let { recyclerViewAdapter?.updateMessagesList(it) }
        return view
    }

    private suspend fun deleteMessageButtonClicked(email: String) {
        withContext(ioDispatcher) {
            viewModel.clearMessages(email)
            withContext(Dispatchers.Main) {
                viewModel.messages.value?.let { recyclerViewAdapter?.updateMessagesList(it) }
            }
        }
    }

    private fun adaptThisFragmentWithRecyclerView(messages: ArrayList<Message>) {
        binding.messageRecyclerview.layoutManager = LinearLayoutManager(this.context)
        recyclerViewAdapter = this.context?.let { context ->
            MessagesListRecyclerViewAdapter(messages, context, parentFragmentManager)
        }
        binding.messageRecyclerview.adapter = recyclerViewAdapter
    }

    private fun observeLiveData() {
        viewModel.messages.observe(viewLifecycleOwner) { arrayOfMessages ->
            arrayOfMessages?.let {
                recyclerViewAdapter?.updateMessagesList(it)
            }
        }
    }
}