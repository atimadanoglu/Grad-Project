package com.graduationproject.grad_project

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.graduationproject.grad_project.databinding.FragmentSignUpMainBinding

class SignUpMainFragment : Fragment() {

    private var _binding: FragmentSignUpMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSignUpMainBinding.inflate(inflater, container, false)
        val view = binding.root
        binding.adminButton.setOnClickListener { goToAdminNewAccountFragment() }
        binding.residentButton.setOnClickListener { goToResidentNewAccountFragment() }
        binding.backButton.setOnClickListener { goToLoginFragment() }
        return view
    }

    private fun goToLoginFragment() {
        val action = SignUpMainFragmentDirections.actionSignUpMainFragmentToLoginFragment()
        findNavController().navigate(action)
    }

    private fun goToResidentNewAccountFragment() {
        val action = SignUpMainFragmentDirections.actionSignUpMainFragmentToResidentNewAccountFragment()
        findNavController().navigate(action)
    }

    private fun goToAdminNewAccountFragment() {
        val action = SignUpMainFragmentDirections.actionSignUpMainFragmentToAdminNewAccountFragment()
        findNavController().navigate(action)
    }

}