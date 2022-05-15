package com.graduationproject.grad_project.view.admin.dialogs

import android.app.Dialog
import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.graduationproject.grad_project.R
import com.graduationproject.grad_project.databinding.FragmentShowExpenditureBinding
import com.graduationproject.grad_project.model.Expenditure
import com.squareup.picasso.Picasso
import java.io.File
import java.util.*

class ShowExpenditureFragment(
    private val expenditure: Expenditure
) : DialogFragment() {

    private var _binding: FragmentShowExpenditureBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            _binding = FragmentShowExpenditureBinding.inflate(layoutInflater)
            val view = binding.root
            val message = "Harcanan Tutar: ${expenditure.amount} TL"
            binding.expenditureContent.text = message
            showImage()
            val uuid = UUID.randomUUID()
            binding.expenditureDocument.setOnClickListener {
                downloadImageNew(uuid.toString(), expenditure.documentUri)
            }
            val dialog = MaterialAlertDialogBuilder(requireContext())
                .setView(view)
                .setTitle(expenditure.title)
                .setPositiveButton(R.string.tamam) { _, _ -> }
                .create()

            dialog
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    private fun showImage() {
        binding.expenditureDocument.visibility = View.GONE
        if (expenditure.documentUri.isNotEmpty()) {
            binding.expenditureDocument.visibility = View.VISIBLE
            Picasso.get().load(expenditure.documentUri).into(binding.expenditureDocument)
        } else {
            binding.expenditureDocument.visibility = View.GONE
        }
    }

    private fun downloadImageNew(filename: String, downloadUrlOfImage: String) {
        try {
            val dm = activity?.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager?
            val downloadUri: Uri = Uri.parse(downloadUrlOfImage)
            val request = DownloadManager.Request(downloadUri)
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(false)
                .setTitle(filename)
                .setMimeType("image/jpeg")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_PICTURES,
                    File.separator.toString() + filename + ".jpg"
                )
            dm!!.enqueue(request)
            Toast.makeText(requireContext(), R.string.dosyaİndiriliyor, Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(requireContext(), R.string.dosyaİndirmeBaşarısız, Toast.LENGTH_SHORT).show()
        }
    }
}