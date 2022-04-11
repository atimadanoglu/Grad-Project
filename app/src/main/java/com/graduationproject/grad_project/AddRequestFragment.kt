package com.graduationproject.grad_project

import android.os.Bundle
import android.text.InputType
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.view.get
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.graduationproject.grad_project.databinding.FragmentAddRequestBinding
import com.graduationproject.grad_project.viewmodel.dialogs.AddRequestViewModel
import kotlinx.coroutines.launch

class AddRequestFragment : Fragment() {

    private var _binding: FragmentAddRequestBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AddRequestViewModel by viewModels()
    companion object {
        const val TAG = "AddRequestFragment"
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddRequestBinding.inflate(inflater, container, false)
        binding.backButtonToRequests.setOnClickListener { goBackToRequests() }
        binding.sendRequestButton.setOnClickListener {
            lifecycleScope.launch {
                sendRequestButtonClicked()
            }
        }
        return binding.root
    }

    private suspend fun sendRequestButtonClicked() {
       try {
           viewModel.setTitle(binding.titleInput.text.toString())
           binding.requestTypeText.setOnItemClickListener { adapterView, view, i, l ->
               when (adapterView.selectedItemId.toInt()) {
                   adapterView[0].id -> {
                       viewModel.setType(adapterView[0].toString())
                       println(adapterView[0].toString())
                   }
                   adapterView[1].id -> {
                       viewModel.setType(adapterView[1].toString())
                       println(adapterView[1].toString())
                   }
               }
           }
           viewModel.setContent(binding.contentInput.text.toString())
           viewModel.shareRequestWithAdmin()
           goBackToRequests()
       } catch (e: Exception) {
           Log.e(TAG, "sendRequestButtonClicked ---> $e")
       }
    }

    private fun goBackToRequests() {
        val action = AddRequestFragmentDirections.actionAddRequestFragmentToRequestFragment()
        findNavController().navigate(action)
    }

    override fun onResume() {
        super.onResume()
        val typeList = resources.getStringArray(R.array.type_list)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.request_dropdown_item, typeList)
        binding.requestTypeText.inputType = InputType.TYPE_NULL
        binding.requestTypeText.setAdapter(arrayAdapter)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}