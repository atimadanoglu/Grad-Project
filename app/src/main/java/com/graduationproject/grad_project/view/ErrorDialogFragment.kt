package com.graduationproject.grad_project.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.graduationproject.grad_project.R
import com.graduationproject.grad_project.databinding.FragmentErrorDialogBinding
import com.graduationproject.grad_project.viewmodel.ErrorDialogViewModel


class ErrorDialogFragment : Fragment() {

    private var _binding: FragmentErrorDialogBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ErrorDialogViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentErrorDialogBinding.inflate(inflater, container, false)
        binding.signOut.setOnClickListener {
            viewModel.signOut()
        }
        viewModel.isSignedOut.observe(viewLifecycleOwner) {
            if (it == null) {
                Snackbar.make(
                    requireView(),
                    R.string.oturumKapatılamadı,
                    Snackbar.LENGTH_LONG
                ).show()
            }
            it?.let {
                if (it) {
                    goToLoginPage()
                }
            }
        }
        return binding.root
    }

    private fun goToLoginPage() {
        val action = ErrorDialogFragmentDirections.actionErrorDialogFragmentToLoginFragment()
        findNavController().navigate(action)
    }
}