package com.graduationproject.grad_project.view.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.graduationproject.grad_project.adapter.AnnouncementRecyclerViewAdapter
import com.graduationproject.grad_project.databinding.FragmentAnnouncementsBinding
import com.graduationproject.grad_project.viewmodel.AnnouncementsViewModel

class AnnouncementsFragment : Fragment() {

    private var _binding : FragmentAnnouncementsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AnnouncementsViewModel by viewModels()

    private lateinit var announcementRecyclerViewAdapter: AnnouncementRecyclerViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentAnnouncementsBinding.inflate(inflater, container, false)
        binding.announcementRecyclerview.layoutManager = LinearLayoutManager(requireContext())
        announcementRecyclerViewAdapter = AnnouncementRecyclerViewAdapter(parentFragmentManager, requireContext())
        binding.announcementRecyclerview.adapter = announcementRecyclerViewAdapter
        binding.lifecycleOwner = viewLifecycleOwner
        viewModel.announcement.observe(viewLifecycleOwner) {
            announcementRecyclerViewAdapter.submitList(it)
        }
        viewModel.retrieveAnnouncements()
        binding.addAnnouncementButton.setOnClickListener {
            val action = AnnouncementsFragmentDirections.actionAnnouncementsFragmentToAddAnnouncementFragment()
            requireView().findNavController().navigate(action)
        }
        return binding.root
    }

}