package com.graduationproject.grad_project.view.resident

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.firebase.auth.FirebaseAuth
import com.graduationproject.grad_project.R
import com.graduationproject.grad_project.viewmodel.WaitingApprovalResidentViewModel


class WaitingApprovalResidentFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private val viewModel: WaitingApprovalResidentViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        auth = FirebaseAuth.getInstance()
        viewModel.checkVerifiedStatus()
        viewModel.isVerified.observe(viewLifecycleOwner) {
            if (it == true) {
                goToResidentHomePage()
            }
        }
        return inflater.inflate(R.layout.fragment_waiting_approval_resident, container, false)
    }

    private fun goToResidentHomePage() {
        val intent = Intent(this.context, HomePageResidentActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        auth.signOut()
    }

}