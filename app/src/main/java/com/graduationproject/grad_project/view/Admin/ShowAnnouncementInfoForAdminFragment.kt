package com.graduationproject.grad_project.view.admin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.graduationproject.grad_project.R
import com.graduationproject.grad_project.databinding.FragmentShowAnnouncementInfoForAdminBinding
import com.graduationproject.grad_project.model.Announcement
import com.squareup.picasso.Picasso


class ShowAnnouncementInfoForAdminFragment : Fragment() {

    private var _binding: FragmentShowAnnouncementInfoForAdminBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentShowAnnouncementInfoForAdminBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.backButtonToAnnouncement.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    fun showAnnouncement(announcements: ArrayList<Announcement>, position: Int) {
        binding.announcementTitleText.text = announcements[position].title
        binding.announcementContentText.text = announcements[position].content
        Picasso.get().load(announcements[position].pictureDownloadUri).into(binding.pictureView)
    }
}