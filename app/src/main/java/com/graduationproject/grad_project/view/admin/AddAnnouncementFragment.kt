package com.graduationproject.grad_project.view.admin

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.graduationproject.grad_project.R
import com.graduationproject.grad_project.databinding.FragmentAddAnnouncementBinding
import com.graduationproject.grad_project.model.Notification
import com.graduationproject.grad_project.viewmodel.AddAnnouncementViewModel
import kotlinx.coroutines.launch
import java.util.*


class AddAnnouncementFragment : Fragment() {

    private var _binding : FragmentAddAnnouncementBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth : FirebaseAuth
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String> // Manifest.permission.READ_EXTERNAL_STORAGE is a String
    private var selectedPicture : Uri? = null

    private val viewModel: AddAnnouncementViewModel by viewModels()

    companion object {
        private const val TAG = "AddAnnouncementFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentAddAnnouncementBinding.inflate(inflater, container, false)
        val view = binding.root
        registerLauncher()
        auth = FirebaseAuth.getInstance()
        updateUI()
        binding.shareAnnouncementButton.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                shareAnnouncementButtonClicked(view)
            }
        }
        binding.takePhoto.setOnClickListener { selectImageButtonClicked(view) }
        binding.backButtonToAnnouncement.setOnClickListener { goToPreviousPage() }
        return view
    }

    private fun updateUI() {
        val title = binding.titleInput.text
        val content = binding.contentInput.text
        viewModel.setTitle(title.toString())
        viewModel.setContent(content.toString())
        if (selectedPicture != null) {
            viewModel.setSelectedPicture(selectedPicture)
        }
        binding.titleInput.setText(viewModel.title)
        binding.contentInput.setText(viewModel.content)
    }

    private fun isBlank(): Boolean {
        return binding.titleInput.text!!.isBlank() || binding.contentInput.text!!.isBlank()
    }

    private suspend fun shareAnnouncementButtonClicked(view: View) {
        val currentUser = auth.currentUser
        val notification = getNotificationInfo()
        if (isBlank()) {
            Toast.makeText(this.context, R.string.boşluklarıDoldur, Toast.LENGTH_LONG).show()
        } else {
            try {
                currentUser?.email?.let { email ->
                    viewModel.makeShareAnnouncementOperation(email, notification, view)
                    goToPreviousPage()
                }
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
            }
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

    private fun getNotificationInfo(): Notification {
        val uuid = UUID.randomUUID()
        return Notification(
            binding.titleInput.text.toString(),
            binding.contentInput.text.toString(),
            selectedPicture.toString(),
            uuid.toString(),
            Timestamp(Date())
        )
    }

    private fun goToPreviousPage() {
        val action = AddAnnouncementFragmentDirections.actionAddAnnouncementFragmentToAnnouncementsFragment()
        findNavController().navigate(action)
    }
}