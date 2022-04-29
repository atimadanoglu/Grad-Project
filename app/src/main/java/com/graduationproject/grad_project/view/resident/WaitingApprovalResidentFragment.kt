package com.graduationproject.grad_project.view.resident

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.firebase.auth.FirebaseAuth
import com.graduationproject.grad_project.databinding.FragmentWaitingApprovalResidentBinding
import com.graduationproject.grad_project.viewmodel.WaitingApprovalResidentViewModel


class WaitingApprovalResidentFragment : Fragment() {

    private var _binding: FragmentWaitingApprovalResidentBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private val viewModel: WaitingApprovalResidentViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentWaitingApprovalResidentBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()
        viewModel.checkVerifiedStatus()
        viewModel.isVerified.observe(viewLifecycleOwner) {
            if (it == true) {
                println("true içi ${it.toString().toBoolean()}")
                goToResidentHomePage()
            }
        }
        return binding.root
    }

    private fun goToResidentHomePage() {
        val intent = Intent(this.context, HomePageResidentActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }
    override fun onDestroy() {
        super.onDestroy()
        viewModel.clear()
    }

}