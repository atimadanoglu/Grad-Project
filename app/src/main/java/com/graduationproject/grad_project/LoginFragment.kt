package com.graduationproject.grad_project

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.graduationproject.grad_project.databinding.FragmentLoginBinding
import com.graduationproject.grad_project.viewmodel.LoginViewModel


class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        val view = binding.root
        binding.LogIn.setOnClickListener { goToResidentHomePageActivity() }
        binding.signUpHereTextButton.setOnClickListener { goToSignUpMainFragment() }
        return view
    }

    private fun goToSignUpMainFragment() {
        val action = LoginFragmentDirections.actionLoginFragmentToSignUpMainFragment()
        findNavController().navigate(action)
    }

    private fun goToResidentHomePageActivity() {
        val action = LoginFragmentDirections.actionLoginFragmentToHomePageResidentActivity()
        findNavController().navigate(action)
    }

}