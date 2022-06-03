package com.graduationproject.grad_project.view.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.graduationproject.grad_project.adapter.CollectionOfPaymentsAdapter
import com.graduationproject.grad_project.databinding.FragmentCollectionOfDebtBinding
import com.graduationproject.grad_project.viewmodel.CollectionOfPaymentsViewModel


class CollectionOfDebtFragment : Fragment() {

    private var _binding: FragmentCollectionOfDebtBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: CollectionOfPaymentsAdapter
    private val viewModel: CollectionOfPaymentsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentCollectionOfDebtBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        adapter = CollectionOfPaymentsAdapter()
        binding.collectionOfDebtRecyclerview.adapter = adapter
        viewModel.retrievePayments()
        viewModel.payments.observe(viewLifecycleOwner) {
            it?.let {
                adapter.submitList(it)
            }
        }
        return binding.root
    }
}