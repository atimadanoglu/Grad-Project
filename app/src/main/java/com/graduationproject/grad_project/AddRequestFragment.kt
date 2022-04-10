package com.graduationproject.grad_project

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.graduationproject.grad_project.databinding.FragmentAddRequestBinding

class AddRequestFragment : Fragment() {

    private var _binding: FragmentAddRequestBinding? = null
    private val binding get() = _binding!!

    override fun onResume() {
        super.onResume()
        val typeList = resources.getStringArray(R.array.type_list)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.request_dropdown_item, typeList)
        binding.requestTypeText.setAdapter(arrayAdapter)

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddRequestBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}