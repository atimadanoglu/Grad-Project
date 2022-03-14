package com.graduationproject.grad_project.view.admin

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.graduationproject.grad_project.R
import com.graduationproject.grad_project.adapter.ResidentsListRecyclerViewAdapter
import com.graduationproject.grad_project.databinding.FragmentResidentsListForAdminBinding
import com.graduationproject.grad_project.model.SiteResident

class ResidentsListForAdminFragment : Fragment() {

    private var _binding : FragmentResidentsListForAdminBinding? = null
    private val binding get() = _binding!!

    private var residentsListRecyclerViewAdapter : ResidentsListRecyclerViewAdapter? = null
    private lateinit var db : FirebaseFirestore
    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        _binding = FragmentResidentsListForAdminBinding.inflate(inflater, container, false)
        retrieveAndShowResidents()
        return binding.root
    }

    private fun retrieveAndShowResidents() {
        val siteResidents = ArrayList<SiteResident>()
        auth.currentUser?.email?.let { email ->
            db.collection("administrators")
                .document(email)
                .get()
                .addOnSuccessListener {
                    val adminInfo = it
                    Log.d("admin", "Admin info retrieved")
                    db.collection("residents")
                        .whereEqualTo("city", adminInfo["city"])
                        .whereEqualTo("district", adminInfo["district"])
                        .whereEqualTo("siteName", adminInfo["siteName"])
                        .get()
                        .addOnSuccessListener { residents ->
                            Log.d("AddAnnouncementFragment", "get() successfully worked")
                                for (resident in residents) {
                                    siteResidents.add(SiteResident(
                                        fullName = resident.get("fullName") as String,
                                        email = resident.get("email") as String,
                                        password = resident.get("password") as String,
                                        phoneNumber = resident.get("phoneNumber") as String,
                                        blockNo = resident.get("blockNo") as String,
                                        flatNo = resident.get("flatNo").toString().toInt(),
                                        debt = resident.get("debt") as Double
                                    ))
                                    binding.recyclerview.layoutManager = LinearLayoutManager(this.context)
                                    residentsListRecyclerViewAdapter = ResidentsListRecyclerViewAdapter(siteResidents)
                                    binding.recyclerview.adapter = residentsListRecyclerViewAdapter
                                }
                        }
                }
        }
    }


}