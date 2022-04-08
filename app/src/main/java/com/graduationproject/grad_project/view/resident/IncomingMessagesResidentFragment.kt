package com.graduationproject.grad_project.view.resident

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.graduationproject.grad_project.R
import com.graduationproject.grad_project.databinding.FragmentIncomingMessagesResidentBinding
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
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
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
        return view
    }

    private suspend fun deleteMessageButtonClicked(email: String) {
        withContext(ioDispatcher) {
            viewModel.clearMessages(email)
        }
    }
}