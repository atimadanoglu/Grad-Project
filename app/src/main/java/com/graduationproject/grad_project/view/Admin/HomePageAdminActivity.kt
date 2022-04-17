package com.graduationproject.grad_project.view.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.graduationproject.grad_project.R
import com.graduationproject.grad_project.databinding.ActivityHomePageAdminBinding
import com.graduationproject.grad_project.databinding.DrawerHeaderBinding
import com.graduationproject.grad_project.view.LoginActivity

class HomePageAdminActivity : AppCompatActivity() {

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
       /* makeToggle()
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // to display icon on action bar*/


        // Switching fragments from bottom navigation
        val navHostFragment =
            binding.mainFragmentContainerView.getFragment() as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavigation.setupWithNavController(navController)
        binding.navigationView.setupWithNavController(navController)
        /*binding.navigationView.setNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.sign_out -> {
                    auth.signOut()
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.notificationsAdminFragment -> true
                else -> true
            }
        }*/

        auth.currentUser?.let { setHeader(it) }
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

    private fun setHeader(currentUser: FirebaseUser) {
        val header = binding.navigationView.getHeaderView(0)
        val drawerHeaderBinding = DrawerHeaderBinding.bind(header)

        drawerHeaderBinding.headerAccountName.text = currentUser.displayName
        drawerHeaderBinding.headerAccountType.setText(R.string.yönetici)
        drawerHeaderBinding.headerEmailAddress.text = currentUser.email
    }

/*    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navHostFragment =
            binding.mainFragmentContainerView.getFragment() as NavHostFragment
        val navController = navHostFragment.navController
        when(item.itemId) {
            R.id.sign_out -> {
                auth.signOut()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }*/



//    override fun onNavigationItemSelected(item: MenuItem): Boolean {
//        when(item.itemId) {
//            R.id.sign_out -> {
//                auth.signOut()
//                val intent = Intent(this, LoginActivity::class.java)
//                startActivity(intent)
//                finish()
//            }
//        }
   /* override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.sign_out -> {
                auth.signOut()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        return true
    }*/

}