//package com.graduationproject.grad_project.view.admin
//
//import android.app.AlertDialog
//import android.os.Bundle
//import android.util.Log
//import android.view.*
//import androidx.fragment.app.Fragment
//import android.widget.EditText
//import android.widget.ImageView
//import android.widget.PopupMenu
//import android.widget.Toast
//import androidx.appcompat.widget.SearchView
//import androidx.appcompat.widget.SearchView.OnQueryTextListener
//import androidx.navigation.fragment.findNavController
//import androidx.recyclerview.widget.LinearLayoutManager
//import com.google.android.gms.tasks.Task
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.firestore.*
//import com.graduationproject.grad_project.R
//import com.graduationproject.grad_project.adapter.ResidentsListRecyclerViewAdapter
//import com.graduationproject.grad_project.databinding.FragmentAddingDebtDialogBinding
//import com.graduationproject.grad_project.databinding.FragmentResidentsListForAdminBinding
//import com.graduationproject.grad_project.model.Site
//import com.graduationproject.grad_project.model.SiteResident
//import kotlin.collections.ArrayList
//import android.view.Menu as Menu1
//
//class ResidentsListForAdminFragment : Fragment() {
//
//    private var _binding : FragmentResidentsListForAdminBinding? = null
//    private val binding get() = _binding!!
//
//    private var residentsListRecyclerViewAdapter : ResidentsListRecyclerViewAdapter? = null
//    private lateinit var db : FirebaseFirestore
//    private lateinit var auth : FirebaseAuth
//    private val siteResidents = ArrayList<SiteResident>()
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        db = FirebaseFirestore.getInstance()
//        auth = FirebaseAuth.getInstance()
//    }
//
//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
//                              savedInstanceState: Bundle?): View {
//        // Inflate the layout for this fragment
//        _binding = FragmentResidentsListForAdminBinding.inflate(inflater, container, false)
//        retrieveAndShowResidents()
//
//        binding.searchBar.setOnQueryTextListener(object : OnQueryTextListener {
//            override fun onQueryTextSubmit(query: String?): Boolean {
//                return false
//            }
//
//            override fun onQueryTextChange(newText: String?): Boolean {
//                filter(newText)
//                return false
//            }
//        })
//        return binding.root
//    }
//
//    // It gives the filtered result of residents
//    private fun filter(newText: String?) {
//        val filteredList = ArrayList<SiteResident>()
//        for (resident in siteResidents) {
//            if (resident.fullName.contains(newText.toString(), true)
//                || resident.flatNo.toString().contains(newText.toString())
//                || resident.blockNo.contains(newText.toString(), true)
//            ) {
//                filteredList.add(resident)
//            }
//        }
//        residentsListRecyclerViewAdapter?.filterList(filteredList)
//    }
//
//    private fun getResidents(adminInfo: DocumentSnapshot) : Task<QuerySnapshot> {
//        return db.collection("residents")
//            .whereEqualTo("city", adminInfo["city"])
//            .whereEqualTo("district", adminInfo["district"])
//            .whereEqualTo("siteName", adminInfo["siteName"])
//            .orderBy("flatNo", Query.Direction.ASCENDING)
//            .get()
//    }
//
//    private fun getAdminInfo(email: String): Task<DocumentSnapshot> {
//        return db.collection("administrators")
//            .document(email)
//            .get()
//    }
//
//    private fun retrieveAndShowResidents() {
//        auth.currentUser?.email?.let { email ->
//            getAdminInfo(email).addOnSuccessListener {
//                val adminInfo = it
//                Log.d("admin", "Admin info retrieved")
//                getResidents(adminInfo)
//                    .addOnSuccessListener { residents ->
//                        Log.d("AddAnnouncementFragment", "get() successfully worked")
//                        for (resident in residents) {
//                            if (resident != null) {
//                                addResident(siteResidents, resident)
//                            }
//                        }
//                        adaptResidentsListRecyclerView(siteResidents)
//                    }
//            }
//        }
//    }
//
//    private fun addResident(siteResidents: ArrayList<SiteResident>, resident: QueryDocumentSnapshot) {
//      /*  siteResidents.add(
//            SiteResident(
//                fullName = resident.get("fullName") as String,
//                email = resident.get("email") as String,
//                password = resident.get("password") as String,
//                phoneNumber = resident.get("phoneNumber") as String,
//                blockNo = resident.get("blockNo") as String,
//                flatNo = resident.get("flatNo").toString().toInt(),
//                debt = resident.get("debt").toString().toLong().toDouble(),
//                playerID = resident.get("player_id").toString()
//            )
//        )*/
//    }
//
//    private fun adaptResidentsListRecyclerView(siteResidents: ArrayList<SiteResident>) {
//        binding.recyclerview.layoutManager = LinearLayoutManager(this.context)
//        residentsListRecyclerViewAdapter =
//            this.context?.let { ResidentsListRecyclerViewAdapter(siteResidents, it, db, auth) }
//        binding.recyclerview.adapter = residentsListRecyclerViewAdapter
//    }
//
//}