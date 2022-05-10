package com.graduationproject.grad_project.view.resident

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.graduationproject.grad_project.R
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
        binding.signUpButton.setOnClickListener {
            viewModel.checkEmailAddress(args.email)
        }
        viewModel.isThereAnyResident.observe(viewLifecycleOwner) {
            if (it == true) {
                goBackToResidentNewAccountFragment()
                Snackbar.make(
                    requireView(),
                    R.string.buEmailAdresiKullanıldı,
                    Snackbar.LENGTH_LONG
                ).show()
            } else {
                goToResidentHomePageButtonClicked()
            }
        }
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
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
            viewModel.clearLists()
            if (!it.isNullOrEmpty()) {
                binding.districts.setText("")
                binding.sites.setText("")
                binding.blockNo.setText("")
                binding.flatNo.setText("")
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
        viewModel.inputDistrict.observe(viewLifecycleOwner) {
            viewModel.clearLists()
            if (!it.isNullOrEmpty()) {
                binding.sites.setText("")
                binding.blockNo.setText("")
                binding.flatNo.setText("")
                viewModel.retrieveAllSitesBasedOnCityAndDistrict()
            }
        }
        viewModel.allSites.observe(viewLifecycleOwner) {
            viewModel.clearLists()
            if (!it.isNullOrEmpty()) {
                binding.sites.setText("")
                viewModel.getSiteNames()
                val arrayAdapterForSites = ArrayAdapter(requireContext(), R.layout.request_dropdown_item, viewModel.allSiteNames)
                binding.sites.inputType = InputType.TYPE_NULL
                binding.sites.setAdapter(arrayAdapterForSites)
            }
        }
        viewModel.inputSiteName.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) {
                binding.blockNo.setText("")
                binding.flatNo.setText("")
                viewModel.retrieveBlockNameAndFlatCount()
            }
        }
        viewModel.totalFlatCount.observe(viewLifecycleOwner) {
            it?.let {
                viewModel.getFlats()
            }
        }
        viewModel.flatList.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) {
                flatAdapter()
            }
        }
        viewModel.blockNames.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) {
                viewModel.getBlockNames()
                val arrayAdapterForBlockNames = ArrayAdapter(requireContext(), R.layout.request_dropdown_item, viewModel.listOfBlocks)
                binding.blockNo.inputType = InputType.TYPE_NULL
                binding.blockNo.setAdapter(arrayAdapterForBlockNames)
            }
        }
    }

    private fun flatAdapter() {
        viewModel.flatList.value?.let {
            val arrayAdapterForFlat = ArrayAdapter(requireContext(), R.layout.request_dropdown_item, it)
            binding.flatNo.inputType = InputType.TYPE_NULL
            binding.flatNo.setAdapter(arrayAdapterForFlat)
        }
    }


    private fun goBackToResidentNewAccountFragment() {
        val action = ResidentSiteInformationFragmentDirections
            .actionResidentSiteInformationFragmentToResidentNewAccountFragment()
        findNavController().navigate(action)
    }
    private fun goToWaitingApprovalPage() {
        val action = ResidentSiteInformationFragmentDirections
            .actionResidentSiteInformationFragmentToWaitingApprovalResidentFragment()
        findNavController().navigate(action)
    }

    private fun goToResidentHomePageButtonClicked() {
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
                updateUserInfo()
                withContext(Dispatchers.Main) {
                    goToWaitingApprovalPage()
                }
            }
        }
    }

    private fun updateUserInfo() {
        runBlocking {
            viewModel.updateUserDisplayName()
        }
    }
}