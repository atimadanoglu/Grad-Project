package com.graduationproject.grad_project.view.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.graduationproject.grad_project.R
import com.graduationproject.grad_project.adapter.ExpendituresListAdapter
import com.graduationproject.grad_project.databinding.FragmentExpendituresBinding
import com.graduationproject.grad_project.viewmodel.ExpendituresViewModel


class ExpendituresFragment : Fragment() {

    private var _binding: FragmentExpendituresBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ExpendituresViewModel by viewModels()
    private var adapter: ExpendituresListAdapter? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_expenditures, container, false)
        viewModel.expenditures.observe(viewLifecycleOwner) { arraylist ->
            arraylist?.let {
                adapter?.submitList(it)
            }
        }
        viewModel.retrieveAllExpendituresWithSnapshot()
        adapter = ExpendituresListAdapter(requireContext())
        binding.expendituresRecyclerview.adapter = adapter
        binding.lifecycleOwner = viewLifecycleOwner
        binding.addExpendituresButton.setOnClickListener {
            goToAddExpendituresFragmentButton()
        }
        return binding.root
    }

    private fun goToAddExpendituresFragmentButton() {
        val action = ExpendituresFragmentDirections.actionExpendituresFragmentToAddExpendituresFragment()
        findNavController().navigate(action)
    }
}