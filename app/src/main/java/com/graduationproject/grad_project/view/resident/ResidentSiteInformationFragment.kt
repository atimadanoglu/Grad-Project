package com.graduationproject.grad_project.view.resident

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
import com.graduationproject.grad_project.databinding.FragmentResidentSiteInformationBinding
import com.graduationproject.grad_project.viewmodel.ResidentSiteInformationViewModel
import kotlinx.coroutines.*

class ResidentSiteInformationFragment(
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : Fragment() {

    private var _binding: FragmentResidentSiteInformationBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ResidentSiteInformationViewModel by viewModels()
    private val args: ResidentSiteInformationFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentResidentSiteInformationBinding.inflate(inflater, container, false)
        val view = binding.root
        binding.backToResidentNewAccountFragmentButton.setOnClickListener { goBackToResidentNewAccountFragment() }
        binding.signUpButton.setOnClickListener { goToResidentHomePageButtonClicked() }
        return view
    }

    private fun goToResidentHomePageActivity() {
        val intent = Intent(this.context, HomePageResidentActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }

    private fun goBackToResidentNewAccountFragment() {
        val action = ResidentSiteInformationFragmentDirections
            .actionResidentSiteInformationFragmentToResidentNewAccountFragment()
        findNavController().navigate(action)
    }

    private fun goToResidentHomePageButtonClicked() {
        setViewModelData()
        lifecycleScope.launch(ioDispatcher) {
            val b = async {
                viewModel.saveResidentIntoDB(
                    args.fullName,
                    args.phoneNumber,
                    args.email,
                    args.password
                )
            }
            if (b.await()) {
                viewModel.saveSiteIntoDB()
                updateUserInfo()
                goToResidentHomePageActivity()
            }
        }
    }

    private fun updateUserInfo() {
        runBlocking {
            viewModel.updateUserDisplayName()
        }
    }

    private fun setViewModelData() {
        viewModel.setSiteName(binding.siteNameText.text.toString())
        viewModel.setCity(binding.cityText.text.toString())
        viewModel.setDistrict(binding.countyText.text.toString())
        viewModel.setBlockNo(binding.blockNoText.text.toString())
        viewModel.setFlatNo(binding.flatNoText.text.toString().toInt())
    }

}