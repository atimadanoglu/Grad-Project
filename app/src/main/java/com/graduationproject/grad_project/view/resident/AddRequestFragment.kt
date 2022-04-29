package com.graduationproject.grad_project.view.resident

import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.graduationproject.grad_project.R
import com.graduationproject.grad_project.databinding.FragmentAddRequestBinding
import com.graduationproject.grad_project.viewmodel.dialogs.AddRequestViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddRequestFragment : Fragment(){

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

    private suspend fun sendRequestButtonClicked(
        ioDispatcher: CoroutineDispatcher = Dispatchers.IO
    ) {
       try {
           if (!isEmpty()) {
               viewModel.setTitle(binding.titleInput.text.toString())
               viewModel.setContent(binding.contentInput.text.toString())
               viewModel.setType(binding.requestTypeText.text.toString())
               CoroutineScope(ioDispatcher).launch {
                   viewModel.shareRequestWithAdmin()
               }.join()
               goBackToRequests()
           } else {
               showSpacesAreEmptySnackBar()
           }
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
        val arrayAdapter =
            this.context?.let { ArrayAdapter(it, R.layout.request_dropdown_item, typeList) }
        binding.requestTypeText.inputType = InputType.TYPE_NULL
        binding.requestTypeText.setAdapter(arrayAdapter)
    }

    private fun showSpacesAreEmptySnackBar() {
        view?.let {
            Snackbar.make(
                it,
                R.string.boşluklarıDoldur,
                Snackbar.LENGTH_LONG
            ).show()
        }
    }
    private fun isEmpty() = binding.titleInput.text.isNullOrEmpty() || binding.contentInput.text.isNullOrEmpty()

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}