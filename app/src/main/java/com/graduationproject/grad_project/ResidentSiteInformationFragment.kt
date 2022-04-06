package com.graduationproject.grad_project

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.graduationproject.grad_project.databinding.FragmentResidentSiteInformationBinding
import com.graduationproject.grad_project.viewmodel.ResidentSiteInformationViewModel

class ResidentSiteInformationFragment : Fragment() {

    private var _binding: FragmentResidentSiteInformationBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ResidentSiteInformationViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentResidentSiteInformationBinding.inflate(inflater, container, false)
        val view = binding.root
        binding.backToResidentNewAccountFragmentButton.setOnClickListener { goBackToResidentNewAccountFragment() }
        binding.signUpButton.setOnClickListener { goToResidentHomePageActivity() }
        return view
    }

    private fun goToResidentHomePageActivity() {
        val action = ResidentSiteInformationFragmentDirections
            .actionResidentSiteInformationFragmentToHomePageResidentActivity()
        findNavController().navigate(action)
    }

    private fun goBackToResidentNewAccountFragment() {
        val action = ResidentSiteInformationFragmentDirections
            .actionResidentSiteInformationFragmentToResidentNewAccountFragment()
        findNavController().navigate(action)
    }

}