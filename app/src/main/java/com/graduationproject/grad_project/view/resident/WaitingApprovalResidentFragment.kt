package com.graduationproject.grad_project.view.resident

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.graduationproject.grad_project.R
import com.graduationproject.grad_project.databinding.FragmentWaitingApprovalResidentBinding
import com.graduationproject.grad_project.model.RegistrationStatus
import com.graduationproject.grad_project.viewmodel.WaitingApprovalResidentViewModel


class WaitingApprovalResidentFragment : Fragment() {

    private var _binding: FragmentWaitingApprovalResidentBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private val viewModel: WaitingApprovalResidentViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentWaitingApprovalResidentBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()
        viewModel.checkStatus()
        viewModel.status.observe(viewLifecycleOwner) {
            it?.let {
                if (it == RegistrationStatus.VERIFIED) {
                    goToResidentHomePage()
                }
                if (it == RegistrationStatus.REJECTED) {
                    goToRejectedResidentPage()
                }
            }
        }
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
        val action = WaitingApprovalResidentFragmentDirections
            .actionWaitingApprovalResidentFragmentToLoginFragment()
        findNavController().navigate(action)
    }

    private fun goToResidentHomePage() {
        val intent = Intent(this.context, HomePageResidentActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }

    private fun goToRejectedResidentPage() {
        val action = WaitingApprovalResidentFragmentDirections
            .actionWaitingApprovalResidentFragmentToErrorDialogFragment()
        findNavController().navigate(action)
        viewModel.navigated()
    }

}