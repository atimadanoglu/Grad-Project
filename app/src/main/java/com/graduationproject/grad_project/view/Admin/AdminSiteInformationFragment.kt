package com.graduationproject.grad_project.view.admin

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.graduationproject.grad_project.databinding.FragmentAdminSiteInformationBinding
import com.graduationproject.grad_project.viewmodel.AdminSiteInformationViewModel
import kotlinx.coroutines.*

class AdminSiteInformationFragment : Fragment() {

    private var _binding: FragmentAdminSiteInformationBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AdminSiteInformationViewModel by viewModels()
    private val args: AdminSiteInformationFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentAdminSiteInformationBinding.inflate(inflater, container, false)
        val view = binding.root
        binding.backToAdminNewAccountFragmentButton.setOnClickListener { goBackToSignMainFragment() }
        binding.signUpButton.setOnClickListener {
            lifecycleScope.launch {
                signUpButtonClicked()
            }
        }
        return view
    }

    private suspend fun signUpButtonClicked(scope: CoroutineDispatcher = Dispatchers.IO) {
        setViewModelData()
        lifecycleScope.launch(scope) {
            val b = async {
                viewModel.saveAdminIntoDB(
                    args.fullName,
                    args.phoneNumber,
                    args.email,
                    args.password
                )
            }
            if (b.await()) {
                viewModel.saveSiteIntoDB()
                viewModel.updateUserDisplayName()
                goToAdminHomePage()
            }
        }
    }

    private fun goBackToSignMainFragment() {
        val action = AdminSiteInformationFragmentDirections
            .actionAdminSiteInformationFragmentToAdminNewAccountFragment()
        findNavController().navigate(action)
    }

    private fun goToAdminHomePage() {
        val intent = Intent(this.context, HomePageAdminActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }

    private fun setViewModelData() {
        viewModel.setSiteName(binding.siteNameText.text.toString())
        viewModel.setCity(binding.cityText.text.toString())
        viewModel.setDistrict(binding.countyText.text.toString())
        viewModel.setBlockCount(binding.blockCountText.text.toString())
        viewModel.setFlatCount(binding.flatCountText.text.toString().toLong())
        viewModel.setMonthlyPayment(binding.monthlyPayment.text.toString().toLong())
    }
}