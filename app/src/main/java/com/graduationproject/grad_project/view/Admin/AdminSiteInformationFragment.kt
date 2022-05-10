package com.graduationproject.grad_project.view.admin

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.graduationproject.grad_project.R
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
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.backToAdminInfoFragment.setOnClickListener { goBackToSignMainFragment() }
        binding.signUpButton.setOnClickListener {
            lifecycleScope.launch {
                signUpButtonClicked()
            }
        }
        return view
    }

    override fun onResume() {
        super.onResume()
        val cities = resources.getStringArray(R.array.cities).toList()
        val izmirDistricts = resources.getStringArray(R.array.izmirDistricts).toList()
        val istanbulDistricts = resources.getStringArray(R.array.istanbulDistricts).toList()
        val ankaraDistricts = resources.getStringArray(R.array.ankaraDistricts).toList()
        val arrayAdapterForCities = ArrayAdapter(requireContext(), R.layout.request_dropdown_item, cities)
        binding.cities.inputType = InputType.TYPE_NULL
        binding.cities.setAdapter(arrayAdapterForCities)

        viewModel.inputCity.observe(viewLifecycleOwner) {
            binding.districts.setText("")
            if (!it.isNullOrEmpty()) {
                val districts: List<String> = when(it) {
                    "İzmir" -> izmirDistricts
                    "İstanbul" -> istanbulDistricts
                    "Ankara" -> ankaraDistricts
                    else -> mutableListOf()
                }
                val arrayAdapterForDistricts = ArrayAdapter(requireContext(), R.layout.request_dropdown_item, districts)
                binding.districts.inputType = InputType.TYPE_NULL
                binding.districts.setAdapter(arrayAdapterForDistricts)
            }
        }
    }


    private suspend fun signUpButtonClicked(scope: CoroutineDispatcher = Dispatchers.IO) {
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
}