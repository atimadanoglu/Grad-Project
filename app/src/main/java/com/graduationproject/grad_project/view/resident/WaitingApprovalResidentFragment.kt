package com.graduationproject.grad_project.view.resident

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.graduationproject.grad_project.R


class WaitingApprovalResidentFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        auth = FirebaseAuth.getInstance()
        return inflater.inflate(R.layout.fragment_waiting_approval_resident, container, false)
    }

    override fun onDestroy() {
        super.onDestroy()
        auth.signOut()
    }

}