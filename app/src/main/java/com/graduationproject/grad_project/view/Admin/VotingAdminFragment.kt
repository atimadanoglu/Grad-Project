package com.graduationproject.grad_project.view.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.graduationproject.grad_project.databinding.FragmentVotingAdminBinding

class VotingAdminFragment : Fragment() {

    private var _binding: FragmentVotingAdminBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentVotingAdminBinding.inflate(inflater, container, false)
        fabSetOnClickListener()
        return binding.root
    }

    private fun fabSetOnClickListener() {
        binding.fab.setOnClickListener {
            val action = VotingAdminFragmentDirections.actionVotingAdminFragmentToAddVotingAdminFragment()
            requireView().findNavController().navigate(action)
        }
    }

}