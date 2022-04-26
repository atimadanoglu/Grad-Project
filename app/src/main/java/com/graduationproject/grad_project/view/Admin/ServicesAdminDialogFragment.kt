package com.graduationproject.grad_project.view.admin

import android.os.Bundle
import android.text.InputType
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.graduationproject.grad_project.R
import com.graduationproject.grad_project.databinding.FragmentServicesAdminDialogBinding


class ServicesAdminDialogFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private var _binding: FragmentServicesAdminDialogBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_services_admin_dialog, container, false)
    }

    override fun onResume() {
        super.onResume()
        val typeList = resources.getStringArray(R.array.serviceType_list)
        val arrayAdapter =
            this.context?.let { ArrayAdapter(it, R.layout.request_dropdown_item, typeList) }
        binding.servicesTypeText.inputType = InputType.TYPE_NULL
        binding.servicesTypeText.setAdapter(arrayAdapter)
        binding.servicesTypeText.onItemSelectedListener = this
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        TODO("Not yet implemented")
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("Not yet implemented")
    }
}