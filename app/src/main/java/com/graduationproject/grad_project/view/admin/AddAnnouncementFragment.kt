package com.graduationproject.grad_project.view.admin

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.graduationproject.grad_project.databinding.FragmentAddAnnouncementBinding
import com.graduationproject.grad_project.viewmodel.AddAnnouncementViewModel


class AddAnnouncementFragment : Fragment() {

    private var _binding : FragmentAddAnnouncementBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth : FirebaseAuth
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String> // Manifest.permission.READ_EXTERNAL_STORAGE is a String
    private var selectedPicture : Uri? = null
    private val viewModel: AddAnnouncementViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentAddAnnouncementBinding.inflate(inflater, container, false)
        val view = binding.root
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        registerLauncher()
        auth = FirebaseAuth.getInstance()
        binding.shareAnnouncementButton.setOnClickListener {
            viewModel.shareAnnouncementClicked()
        }
        viewModel.isShareAnnouncementButtonClicked.observe(viewLifecycleOwner) {
            it?.let {
                if (it) {
                    viewModel.uploadImageAndShareNotification(selectedPicture)
                    Snackbar.make(
                        binding.shareAnnouncementButton,
                        "Duyurunuz sakinlerle paylaşılıyor...",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }
        }
        viewModel.notification.observe(viewLifecycleOwner) {
            it?.let {
                viewModel.saveAnnouncementIntoDB()
            }
        }
        viewModel.playerIDs.observe(viewLifecycleOwner) {
            it?.let {
                if (it.isNotEmpty()) {
                    viewModel.sendPushNotification(it)
                }
            }
        }
        binding.takePhoto.setOnClickListener { selectImageButtonClicked(view) }
        binding.backButtonToAnnouncement.setOnClickListener { goToPreviousPage() }
        viewModel.isShared.observe(viewLifecycleOwner) {
            it?.let {
                if (it) {
                    goToPreviousPage()
                }
            }
        }
        return view
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

    private fun selectImageButtonClicked(view : View) {
        if (ContextCompat
                .checkSelfPermission(
                    view.context,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Snackbar.make(view, "Permission needed for gallery", Snackbar.LENGTH_INDEFINITE)
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

    private fun goToPreviousPage() {
        val action = AddAnnouncementFragmentDirections.actionAddAnnouncementFragmentToAnnouncementsFragment()
        findNavController().navigate(action)
    }
}