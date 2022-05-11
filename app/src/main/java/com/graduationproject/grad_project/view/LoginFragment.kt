package com.graduationproject.grad_project.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.graduationproject.grad_project.R
import com.graduationproject.grad_project.databinding.FragmentLoginBinding
import com.graduationproject.grad_project.view.admin.HomePageAdminActivity
import com.graduationproject.grad_project.view.resident.HomePageResidentActivity
import com.graduationproject.grad_project.viewmodel.LoginViewModel

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LoginViewModel by viewModels()
    private lateinit var auth: FirebaseAuth

    companion object {
        private const val TAG = "LoginFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        val view = binding.root
        auth = FirebaseAuth.getInstance()

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.signUpHereTextButton.setOnClickListener { goToSignUpMainFragment() }

        viewModel.isSignedIn.observe(viewLifecycleOwner) {
            if (it == null) {
                Snackbar.make(
                    requireView(),
                    R.string.lütfenEmailVeŞifreniziKontrolEdiniz,
                    Snackbar.LENGTH_LONG
                ).show()
            }
            it?.let {
                if (it) {
                    viewModel.isResident()
                    viewModel.isAdmin()
                } else {
                    Log.d(TAG, "viewModel.isSignedIn.observe --> There is no signed-in user")
                }
            }
        }
        viewModel.isAdmin.observe(viewLifecycleOwner) {
            it?.let {
                if (it) {
                    if (viewModel.goToAdminHomePageRule())
                        goToAdminHomePageActivity()
                }
            }
        }
        viewModel.isResident.observe(viewLifecycleOwner) {
            it?.let {
                if (it) {
                    viewModel.checkStatus()
                }
            }
        }
        viewModel.registrationStatus.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) {
                if (viewModel.goToResidentHomePageRule()) {
                    goToResidentHomePageActivity()
                }
                if (viewModel.goToRejectedResidentPageRule()) {
                    goToRejectedResidentPage()
                }
                if (viewModel.goToPendingApprovalPageRule()) {
                    goToWaitingApprovalPage()
                }
            }
        }
        return view
    }

    private fun goToSignUpMainFragment() {
        val action = LoginFragmentDirections.actionLoginFragmentToSignUpMainFragment()
        findNavController().navigate(action)
    }

    private fun goToResidentHomePageActivity() {
        val intent = Intent(this.context, HomePageResidentActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }

    private fun goToAdminHomePageActivity() {
        val intent = Intent(this.context, HomePageAdminActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }

    private fun goToWaitingApprovalPage() {
        val action = LoginFragmentDirections.actionLoginFragmentToWaitingApprovalResidentFragment()
        requireView().findNavController().navigate(action)
    }

    private fun goToRejectedResidentPage() {
        val action = LoginFragmentDirections.actionLoginFragmentToErrorDialogFragment()
        findNavController().navigate(action)
    }
}