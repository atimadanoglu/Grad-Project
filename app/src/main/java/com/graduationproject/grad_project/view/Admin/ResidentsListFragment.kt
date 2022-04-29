package com.graduationproject.grad_project.view.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.graduationproject.grad_project.adapter.ResidentsListAdapter
import com.graduationproject.grad_project.databinding.FragmentResidentsListBinding
import com.graduationproject.grad_project.viewmodel.ResidentsListViewModel

class ResidentsListFragment : Fragment() {

    private var _binding: FragmentResidentsListBinding? = null
    private val binding get() = _binding!!
    private var residentsListAdapter : ResidentsListAdapter? = null
    private val viewModel: ResidentsListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentResidentsListBinding.inflate(inflater, container, false)
        val view = binding.root
        viewModel.residentsList.observe(viewLifecycleOwner) {
            residentsListAdapter?.submitList(it)
        }
        viewModel.getResidentsInASpecificSiteWithSnapshot()
        residentsListAdapter = ResidentsListAdapter(parentFragmentManager, requireContext())
        binding.recyclerview.adapter = residentsListAdapter
        binding.lifecycleOwner = viewLifecycleOwner

        return view
    }
}