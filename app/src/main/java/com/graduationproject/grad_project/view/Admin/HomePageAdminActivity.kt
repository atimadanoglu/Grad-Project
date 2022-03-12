package com.graduationproject.grad_project.view.admin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.core.view.get
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.graduationproject.grad_project.R
import com.graduationproject.grad_project.databinding.ActivityHomePageAdminBinding

class HomePageAdminActivity : AppCompatActivity() {

    private lateinit var binding : ActivityHomePageAdminBinding
    private lateinit var db : FirebaseFirestore
    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomePageAdminBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // Switching fragments from bottom navigation
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.mainFragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController
        findViewById<BottomNavigationView>(R.id.bottom_navigation)
            .setupWithNavController(navController)


        retrieveAndUpdateHeaderInfoFromDB()


    }


    // Retrieve fullName, user type and email from db
    private fun retrieveAndUpdateHeaderInfoFromDB() {

        // if you don't do this operation, you will get a NPE
        val header = binding.navigationView.getHeaderView(0)

        // You need to take header as a reference, otherwise it won't work
        val headerAccountName : TextView? = header.findViewById<TextView>(R.id.headerAccountName)
        val headerAccountType : TextView? = header.findViewById<TextView>(R.id.headerAccountType)
        val headerEmailAddress : TextView? = header.findViewById<TextView>(R.id.headerEmailAddress)

        if (headerAccountName == null) {
            println("account name is null")
        } else {
            println("is not null")
        }

        val currentUser = auth.currentUser
        if (currentUser != null) {
            currentUser.email?.let { email ->
                db.collection("administrators")
                    .document(email)
                    .get()
                    .addOnSuccessListener { result ->
                        Log.d("HomePageAdminActivity", "Reading user info is successful!!!")
                        println("result is $result")
                        if (result != null) {
                            headerAccountName!!.text = result["fullName"] as String
                            headerAccountType!!.text = result["typeOfUser"] as String
                            headerEmailAddress!!.text = result["email"] as String
                        }
                    }
            }
        }
    }

}