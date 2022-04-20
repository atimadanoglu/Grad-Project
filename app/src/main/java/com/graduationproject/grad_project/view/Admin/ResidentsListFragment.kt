package com.graduationproject.grad_project.view.admin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.graduationproject.grad_project.adapter.ResidentsListAdapter
import com.graduationproject.grad_project.databinding.FragmentResidentsListBinding
import com.graduationproject.grad_project.viewmodel.ResidentsListViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ResidentsListFragment(
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : Fragment() {

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
        listResidents()
        adaptResidentsListRecyclerView()
        viewModel.residentsList.observe(viewLifecycleOwner) {
            residentsListAdapter?.submitList(it)
        }
        viewModel.filteredList.observe(viewLifecycleOwner) {
            residentsListAdapter?.submitList(it)
        }

        binding.searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    filter(newText)
                }
                return false
            }
        })
        return view
    }

    private fun listResidents() {
        viewLifecycleOwner.lifecycleScope.launch(ioDispatcher) {
            viewModel.retrieveAndShowResidents()
            residentsListAdapter?.submitList(viewModel.residentsList.value)
        }
    }

    private fun adaptResidentsListRecyclerView() {
        binding.recyclerview.layoutManager = LinearLayoutManager(this.context)
        residentsListAdapter =
            context?.let { context ->
                ResidentsListAdapter(
                    parentFragmentManager,
                    context
                )
            }
        binding.recyclerview.adapter = residentsListAdapter
    }

    private fun filter(query: String) {
        viewModel.filter(query)
    }
}