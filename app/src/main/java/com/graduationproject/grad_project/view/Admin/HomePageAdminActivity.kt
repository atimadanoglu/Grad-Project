package com.graduationproject.grad_project.view.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.graduationproject.grad_project.R
import com.graduationproject.grad_project.databinding.ActivityHomePageAdminBinding
import com.graduationproject.grad_project.view.LoginActivity

class HomePageAdminActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding : ActivityHomePageAdminBinding
    private lateinit var db : FirebaseFirestore
    private lateinit var auth : FirebaseAuth
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var adminReference: CollectionReference

    companion object {
        private const val TAG = "HomePageAdminActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomePageAdminBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        adminReference = db.collection("administrators")
        // To show toggle icon on actionBar
        makeToggle()
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // to display icon on action bar

        // Switching fragments from bottom navigation
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.mainFragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavigation.setupWithNavController(navController)

        retrieveAndUpdateHeaderInfoFromDB()
        // Initialized navigation view
        val navigationView: NavigationView = findViewById(R.id.navigation_view)
        // Used implements OnNavigationItemSelectedListener(), it gave us onNavigationItemSelected(item: MenuItem) method
        navigationView.setNavigationItemSelectedListener(this)
    }

    private fun makeToggle() {
        toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            R.string.aç,
            R.string.kapat
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // it enables toggle button
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    // Retrieve fullName, user type and email from db
    private fun retrieveAndUpdateHeaderInfoFromDB() {
        // if you don't do this operation, you will get a NPE
        val header = binding.navigationView.getHeaderView(0)

        // You need to take header as a reference, otherwise it won't work
        val headerAccountName : TextView? = header.findViewById<TextView>(R.id.headerAccountName)
        val headerAccountType : TextView? = header.findViewById<TextView>(R.id.headerAccountType)
        val headerEmailAddress : TextView? = header.findViewById<TextView>(R.id.headerEmailAddress)

        val currentUser = auth.currentUser
        if (currentUser != null) {
            currentUser.email?.let { email ->
                adminReference.document(email)
                    .get()
                    .addOnSuccessListener { result ->
                        Log.d(TAG, "Reading user info is successful!!!")
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

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.sign_out -> {
                auth.signOut()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
        return true
    }

}