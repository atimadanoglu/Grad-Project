package com.graduationproject.grad_project.view.admin

import android.app.Dialog
import android.os.Bundle
import android.text.InputType
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Timestamp
import com.graduationproject.grad_project.R
import com.graduationproject.grad_project.databinding.FragmentServicesAdminDialogBinding
import com.graduationproject.grad_project.model.Service
import com.graduationproject.grad_project.viewmodel.ServicesAdminDialogViewModel
import java.util.*


class ServicesAdminDialogFragment : DialogFragment() {

    private var _binding: FragmentServicesAdminDialogBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ServicesAdminDialogViewModel by viewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            _binding = FragmentServicesAdminDialogBinding.inflate(layoutInflater)
            val view = binding.root
            val materialDialog = MaterialAlertDialogBuilder(requireContext())
                .setView(view)
                .setTitle("Servis Ekle")
                .setPositiveButton(R.string.ekle) { _, _ ->
                    positiveButtonClicked()
                }.create()

            materialDialog
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    private fun positiveButtonClicked() {
        val uuid = UUID.randomUUID()
        viewModel.setName(binding.servicesNameEditText.text.toString())
        viewModel.setPhoneNumber(binding.servicePhoneEditText.text.toString())
        viewModel.setType(binding.servicesTypeText.text.toString())
        if (!viewModel.areNull()) {
            viewModel.addService(Service(
                uuid.toString(),
                viewModel.name.value!!,
                viewModel.phoneNumber.value!!,
                viewModel.type.value!!,
                Timestamp(Date())
                )
            )
        }
    }

    override fun onResume() {
        super.onResume()
        val typeList = resources.getStringArray(R.array.serviceType_list).toList()
        println("type list ${typeList.get(0)}")
        val arrayAdapter =
            this.context?.let { ArrayAdapter(it, R.layout.request_dropdown_item, typeList) }
        binding.servicesTypeText.inputType = InputType.TYPE_NULL
        binding.servicesTypeText.setAdapter(arrayAdapter)
    }
}