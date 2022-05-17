package com.graduationproject.grad_project.view.resident

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.graduationproject.grad_project.R
import com.graduationproject.grad_project.databinding.FragmentAddRequestBinding
import com.graduationproject.grad_project.viewmodel.dialogs.AddRequestViewModel

class AddRequestFragment : Fragment(){

    private var _binding: FragmentAddRequestBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AddRequestViewModel by viewModels()
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String> // Manifest.permission.READ_EXTERNAL_STORAGE is a String
    private var selectedPicture : Uri? = null
    companion object {
        const val TAG = "AddRequestFragment"
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddRequestBinding.inflate(inflater, container, false)
        binding.photoAddText.visibility = View.GONE
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        registerLauncher()
        observe()
        setOnClickListeners()
        viewModel.selectedImage.observe(viewLifecycleOwner) {
            if (it == null) {
                binding.photoAddText.visibility = View.GONE
            }
            it?.let {
                binding.photoAddText.visibility = View.VISIBLE
            }
        }
        return binding.root
    }

    private fun observe() {
        viewModel.request.observe(viewLifecycleOwner) {
            it?.let {
                viewModel.saveTheseDataIntoDB()
                goBackToRequests()
            }
        }
        viewModel.isImageUriNull.observe(viewLifecycleOwner) {
            it?.let {
                viewModel.createRequest()
            }
        }
        viewModel.resident.observe(viewLifecycleOwner) {
            it?.let {
                viewModel.getAdminInfo()
            }
        }
        viewModel.admin.observe(viewLifecycleOwner) {
            it?.let {
                viewModel.setPlayerID(it.player_id)
            }
        }
        viewModel.playerId.observe(viewLifecycleOwner) {
            it?.let {
                viewModel.sendPushNotification()
                viewModel.uploadImageAndShareNotification(selectedPicture)
            }
        }
        viewModel.notification.observe(viewLifecycleOwner) {
            it?.let {
                viewModel.sendPushNotification()
            }
        }
    }

    private fun setOnClickListeners() {
        binding.addPhotoButton.setOnClickListener {
            selectImageButtonClicked()
        }

        binding.backButtonToRequests.setOnClickListener { goBackToRequests() }
        binding.sendRequestButton.setOnClickListener {
            sendRequestButtonClicked()
        }
    }

    private fun sendRequestButtonClicked() {
        showErrorMessages()
        if (!viewModel.isNull()) {
            viewModel.getResidentInfo()
            viewModel.createNotification()
            Snackbar.make(
                requireView(),
                "Talep yöneticiye iletiliyor...",
                Snackbar.LENGTH_LONG
            ).show()
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

    private fun showErrorMessages() {
        if (binding.titleInput.text.isNullOrEmpty()) {
            binding.textInputLayoutTitle.error = " "
        } else {
            binding.textInputLayoutTitle.error = null
        }
        if (binding.contentInput.text.isNullOrEmpty()) {
            binding.textInputLayoutContent.error = " "
        } else {
            binding.textInputLayoutContent.error = null
        }
        if (binding.requestTypeText.text.isNullOrEmpty()) {
            binding.textInputLayoutType.error = " "
        } else {
            binding.textInputLayoutType.error = null
        }
    }

    private fun registerLauncher() {
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intentFromResult = result.data
                if (intentFromResult != null) {
                    selectedPicture = intentFromResult.data
                    viewModel.setSelectedPicture(selectedPicture)
                }
            }
        }

        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
            if (result) {
                // permission granted
                val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            } else {
                Toast.makeText(this.context, "Permission needed", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun selectImageButtonClicked() {
        if (ContextCompat
                .checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Snackbar.make(requireView(), "Permission needed for gallery", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Give permission") {
                        //request permission
                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                    }.show()
            } else {
                // request permission
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }else {
            val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            //start activity for result
            activityResultLauncher.launch(intentToGallery)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}