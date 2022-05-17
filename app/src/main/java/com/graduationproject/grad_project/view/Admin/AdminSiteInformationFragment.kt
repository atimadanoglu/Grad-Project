package com.graduationproject.grad_project.view.admin

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.graduationproject.grad_project.R
import com.graduationproject.grad_project.databinding.FragmentAdminSiteInformationBinding
import com.graduationproject.grad_project.viewmodel.AdminSiteInformationViewModel

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
        binding.backToAdminInfoFragment.setOnClickListener { goBackToAdminNewAccountFragment() }
        binding.signUpButton.setOnClickListener {
            checkBlockName()
            checkFlatCount()
            checkSiteName()
            checkMonthlyPayment()
            if (!viewModel.isEmpty()) {
                viewModel.setAdminInfo(args.fullName, args.phoneNumber, args.email)
                viewModel.checkEmailAddress(args.email)
            } else {
                Snackbar.make(
                    requireView(),
                    "Lütfen boşlukları doldurunuz!",
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }

        viewModel.isThereAnyAdmin.observe(viewLifecycleOwner) {
            it?.let {
                if (it) {
                    Snackbar.make(
                        requireView(),
                        R.string.buEmailAdresiKullanıldı,
                        Snackbar.LENGTH_LONG
                    ).show()
                    goBackToAdminNewAccountFragment()
                } else {
                    viewModel.registerUserWithEmailAndPassword(args.email, args.password)
                }
            }
        }

        viewModel.isUserCreated.observe(viewLifecycleOwner) {
            it?.let {
                if (it) {
                    viewModel.saveAdminInfoToDB()
                    viewModel.saveSiteIntoDB()
                    /*viewModel.updateUserDisplayName()*/
                    goToAdminHomePage()
                }
            }
        }
        return view
    }

    private fun checkSiteName() {
        if (binding.sites.text.isNullOrBlank()) {
            val error = resources.getString(R.string.lütfenBirSiteAdıYazınız)
            binding.sitesLayout.error = error
        } else {
            binding.sitesLayout.error = null
        }
    }

    private fun checkBlockName() {
        if (binding.blockNo.text.isNullOrBlank()) {
            val error = resources.getString(R.string.lütfenBlokAdınıYazınız)
            binding.blockNoLayout.error = error
        } else {
            binding.blockNoLayout.error = null
        }
    }

    private fun checkFlatCount() {
        if (binding.flatNo.text.isNullOrBlank()) {
            val error = resources.getString(R.string.lütfenDaireSayınısıYazınız)
            binding.flatNoLayout.error = error
        } else {
            binding.flatNoLayout.error = null
        }
    }

    private fun checkMonthlyPayment() {
        if (binding.monthlyPayment.text.isNullOrBlank()) {
            val error = resources.getString(R.string.lütfenAidatMiktarınıYazınız)
            binding.monthlyPaymentLayout.error = error
        } else {
            binding.monthlyPaymentLayout.error = null
        }
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

    private fun goBackToAdminNewAccountFragment() {
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