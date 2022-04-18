package com.graduationproject.grad_project.view.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.graduationproject.grad_project.adapter.ExpendituresListAdapter
import com.graduationproject.grad_project.databinding.FragmentExpendituresBinding
import com.graduationproject.grad_project.viewmodel.ExpendituresViewModel


class ExpendituresFragment : Fragment() {

    private var _binding: FragmentExpendituresBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ExpendituresViewModel by viewModels()
    private val adapter: ExpendituresListAdapter by lazy {
        ExpendituresListAdapter(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentExpendituresBinding.inflate(inflater, container, false)
        viewModel.getExpenditures()
        binding.addExpendituresButton.setOnClickListener {
            goToAddExpendituresFragmentButton()
        }
        binding.expendituresRecyclerview.layoutManager = LinearLayoutManager(this.context)
        binding.expendituresRecyclerview.adapter = adapter
        binding.lifecycleOwner = viewLifecycleOwner
        viewModel.expenditures.observe(viewLifecycleOwner) { arraylist ->
            arraylist?.let {
                adapter.setNewList(it)
            }
        }
        return binding.root
    }

    private fun goToAddExpendituresFragmentButton() {
        val action = ExpendituresFragmentDirections.actionExpendituresFragmentToAddExpendituresFragment()
        findNavController().navigate(action)
    }
}