package com.graduationproject.grad_project.view.resident

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.graduationproject.grad_project.adapter.ExpendituresListResidentAdapter
import com.graduationproject.grad_project.databinding.FragmentSiteExpendituresResidentBinding
import com.graduationproject.grad_project.viewmodel.SiteExpendituresResidentViewModel

class SiteExpendituresResidentFragment : Fragment() {

    private var _binding: FragmentSiteExpendituresResidentBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: ExpendituresListResidentAdapter
    private val viewModel: SiteExpendituresResidentViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSiteExpendituresResidentBinding.inflate(inflater, container, false)
        viewModel.expenditures.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
        viewModel.retrieveExpenditures()
        adapter = ExpendituresListResidentAdapter()
        binding.siteExpenditureRecyclerview.adapter = adapter
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

}