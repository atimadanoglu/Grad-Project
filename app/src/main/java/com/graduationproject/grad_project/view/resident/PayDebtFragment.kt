package com.graduationproject.grad_project.view.resident

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.graduationproject.grad_project.R
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
        binding.innerConstraintLayout.visibility = View.GONE
        binding.youDontHaveDebt.visibility = View.VISIBLE
        binding.picturesContraintLayout.visibility = View.VISIBLE
        viewModel.setHighestValue()
        binding.paymentButton.setOnClickListener {
            lifecycleScope.launch {
                payDebtButtonClicked()
            }
        }
        binding.amountEditText.addTextChangedListener {
            checkValidityOfAmount(it)
        }
        viewModel.currentDebtAmount.observe(viewLifecycleOwner) {
            it?.let {
                if (it == 0L) {
                    binding.innerConstraintLayout.visibility = View.GONE
                    binding.youDontHaveDebt.visibility = View.VISIBLE
                    binding.picturesContraintLayout.visibility = View.VISIBLE
                } else {
                    binding.innerConstraintLayout.visibility = View.VISIBLE
                    binding.youDontHaveDebt.visibility = View.GONE
                    binding.picturesContraintLayout.visibility = View.VISIBLE
                }
            }
        }
        return binding.root
    }

    private suspend fun payDebtButtonClicked() {
        lifecycleScope.launch {
            val bottomNavigationView = activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)
            if (!isEmpty()) {
                isValidCVC()
                isValidCardNumber()
                isValidExpirationDate()
                setValues()
                if (isChecked()) {
                    if (isAllValid()) {
                        val text = "İşleminiz gerçekleştiriliyor..."
                        Snackbar.make(requireView(), text, 2000)
                            .setAnchorView(bottomNavigationView)
                            .show()
                        delay(2500L)
                        viewModel.payDebt()
                        viewModel.savePayment()
                        goToPayDebtPage()
                        return@launch
                    }
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
    }

    private fun checkValidityOfAmount(text: Editable?) {
        binding.textInputLayoutAmount.error = null
        val highestAmount = viewModel.highestAmount.value
        if (text?.isNotEmpty() == true) {
            highestAmount?.let { value ->
                if (text.toString().toInt() in 1..value) {
                    binding.textInputLayoutAmount.error = null
                    viewModel.setIsValidAmount(true)
                } else {
                    binding.textInputLayoutAmount.error = "Geçersiz tutar!"
                    viewModel.setIsValidAmount(false)
                }
            }
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

    private fun goToPayDebtPage() {
        val amount = viewModel.amount.value
        amount?.let {
            val action = PayDebtFragmentDirections.actionPayDebtFragmentToSuccessfulDialogFragment(it)
            requireView().findNavController().navigate(action)
        }
    }

    private fun isChecked() = binding.checkBox.isChecked

    private fun isValidCVC() {
        if (binding.cvcEditText.text.toString().length == 3) {
            viewModel.setIsValidCVC(true)
            binding.textInputLayoutCVC.error = null
        } else {
            binding.textInputLayoutCVC.error = "Geçersiz!"
        }
    }

    private fun isValidCardNumber() {
        if (binding.cardNumberEditText.text.toString().length == 16) {
            viewModel.setIsValidCardNumber(true)
            binding.textInputLayoutNumber.error = null
        } else {
            binding.textInputLayoutNumber.error = "Geçersiz!"
        }
    }

    private fun isValidExpirationDate() {
        val expirationDate = binding.expirationDate.text.toString()
        if (expirationDate.length == 5) {
            val month = expirationDate.substringBefore("/")
            val year = expirationDate.substringAfter("/")
            if (month.toInt() in 0..12 && year.toInt() in 22..35) {
                viewModel.setIsValidExpirationDate(true)
                binding.textInputLayoutCalendar.error  = null
            } else {
                binding.textInputLayoutCalendar.error = "Geçersiz!"
            }
        } else {
            binding.textInputLayoutCalendar.error = "Geçersiz!"
        }
    }

    private fun isAllValid(): Boolean {
        return viewModel.isValidCVC.value == true && viewModel.isValidCardNumber.value == true
                && viewModel.isValidExpirationDate.value == true && viewModel.isValidAmount.value == true
    }

}