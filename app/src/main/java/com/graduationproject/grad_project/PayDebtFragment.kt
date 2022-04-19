package com.graduationproject.grad_project

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.graduationproject.grad_project.databinding.FragmentPayDebtBinding
import com.graduationproject.grad_project.viewmodel.PayDebtViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class PayDebtFragment : Fragment() {

    private var _binding: FragmentPayDebtBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PayDebtViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentPayDebtBinding.inflate(inflater, container, false)
        binding.paymentButton.setOnClickListener {
            lifecycleScope.launch {
                payDebtButtonClicked()
            }
        }
        return binding.root
    }

    private suspend fun payDebtButtonClicked() {
        val bottomNavigationView = activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)
        if (!isEmpty()) {
            setValues()
            if (isChecked()) {
                viewModel.payDebt()
                val text = "İşleminizi gerçekleştiriyoruz..."
                Snackbar.make(requireView(), text, Snackbar.LENGTH_SHORT)
                    .setAnchorView(bottomNavigationView)
                    .show()
                delay(3100L)
                goToHomePage()
                return
            } else {
                val text = "Lütfen şartları ve koşulları kabul edin!"
                Snackbar.make(requireView(), text, 2500)
                    .setAnchorView(bottomNavigationView)
                    .show()
            }
        } else {
            Snackbar.make(requireView(), R.string.boşluklarıDoldur, Snackbar.LENGTH_SHORT)
                .setAnchorView(bottomNavigationView).show()
        }
    }

    private fun setValues() {
        viewModel.setValues(
            binding.amountEditText.text.toString().toInt(),
            binding.cardNameEditText.text.toString(),
            binding.cardNumberEditText.text.toString(),
            binding.expirationDate.text.toString(),
            binding.cvcEditText.text.toString().toInt()
        )
    }

    private fun isEmpty() = binding.amountEditText.text.isNullOrEmpty() || binding.cardNameEditText.text.isNullOrEmpty()
            || binding.cardNumberEditText.text.isNullOrEmpty() || binding.expirationDate.text.isNullOrEmpty()
            || binding.cvcEditText.text.isNullOrEmpty()

    private fun goToHomePage() {
        val action = PayDebtFragmentDirections.actionPayDebtFragmentToHomeResidentFragment()
        requireView().findNavController().navigate(action)
    }

    private fun isChecked() = binding.checkBox.isChecked

}