package com.graduationproject.grad_project.view.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.graduationproject.grad_project.R
import com.graduationproject.grad_project.databinding.ActivityHomePageAdminBinding
import com.graduationproject.grad_project.databinding.DrawerHeaderBinding
import com.graduationproject.grad_project.view.MainActivity

class HomePageAdminActivity : AppCompatActivity() {

    private lateinit var binding : ActivityHomePageAdminBinding
    private lateinit var db : FirebaseFirestore
    private lateinit var auth : FirebaseAuth
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
       /* makeToggle()
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // to display icon on action bar*/


        // Switching fragments from bottom navigation
        val navHostFragment =
            binding.mainFragmentContainerView.getFragment() as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavigation.setupWithNavController(navController)
        binding.navigationView.setupWithNavController(navController)
        val header = binding.navigationView.getHeaderView(0)
        val drawerHeaderBinding = DrawerHeaderBinding.bind(header)
        drawerHeaderBinding.signOut.setOnClickListener {
            showAlertMessage()
        }
        auth.currentUser?.let { setHeader(it) }
    }

    private fun setHeader(currentUser: FirebaseUser) {
        val header = binding.navigationView.getHeaderView(0)
        val drawerHeaderBinding = DrawerHeaderBinding.bind(header)

        drawerHeaderBinding.headerAccountName.text = currentUser.displayName
        drawerHeaderBinding.headerAccountType.setText(R.string.yönetici)
        drawerHeaderBinding.headerEmailAddress.text = currentUser.email
    }

    private fun showAlertMessage() {
        MaterialAlertDialogBuilder(this)
            .setMessage(R.string.eminMisiniz)
            .setPositiveButton(R.string.evet) { _, _ ->
                auth.signOut()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }.setNegativeButton(R.string.hayır) { _, _ -> }
            .create().show()
    }

}