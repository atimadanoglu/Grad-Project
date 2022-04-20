package com.graduationproject.grad_project.view

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.graduationproject.grad_project.R
import com.graduationproject.grad_project.databinding.FragmentLoginBinding
import com.graduationproject.grad_project.view.admin.HomePageAdminActivity
import com.graduationproject.grad_project.view.resident.HomePageResidentActivity
import com.graduationproject.grad_project.viewmodel.LoginViewModel
import kotlinx.coroutines.*

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LoginViewModel by viewModels()
    private val auth = FirebaseAuth.getInstance()

    companion object {
        private const val TAG = "LoginFragment"
    }

    override fun onStart() {
        super.onStart()
        /*auth.signOut()*/
        if (auth.currentUser != null) {
            viewModel.setIsSignedIn(true)
            viewModel.setEmail(auth.currentUser!!.email.toString())
        }
        checkIfThereIsAnyUserSignedInAndDirectToPage()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        val view = binding.root
        binding.LogIn.setOnClickListener {
            loginButtonClicked()
        }
        binding.signUpHereTextButton.setOnClickListener { goToSignUpMainFragment() }
        return view
    }

    private fun goToSignUpMainFragment() {
        val action = LoginFragmentDirections.actionLoginFragmentToSignUpMainFragment()
        findNavController().navigate(action)
    }

    private fun goToResidentHomePageActivity() {
        val intent = Intent(this.context, HomePageResidentActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }

    private fun goToAdminHomePageActivity() {
        val intent = Intent(this.context, HomePageAdminActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }

    private fun isEmpty() = binding.TextEmailAddress.text!!.isEmpty() || binding.TextPassword.text.isEmpty()

    private fun loginButtonClicked(
        ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
        mainDispatcher: CoroutineDispatcher = Dispatchers.Main
    ) {
        lifecycleScope.launch {
            if (!isEmpty()) {
                withContext(ioDispatcher) {
                    viewModel.setPassword(binding.TextPassword.text.toString())
                    viewModel.setEmail(binding.TextEmailAddress.text.toString())
                    val email = async { viewModel.email }
                    val password = async { viewModel.password }
                    viewModel.makeLoginOperation(email.await(), password.await(), view)
                }
                if (viewModel.typeOfUser == "Yönetici" && auth.currentUser != null) {
                    withContext(mainDispatcher) {
                        goToAdminHomePageActivity()
                    }
                }
                if (viewModel.typeOfUser == "Sakin"  && auth.currentUser != null) {
                    withContext(mainDispatcher) {
                        goToResidentHomePageActivity()
                    }
                }
            } else {
                view?.let {
                    Snackbar.make(
                        it,
                        R.string.boşluklarıDoldur,
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }

        }
    }

    private fun checkIfThereIsAnyUserSignedInAndDirectToPage(
        ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
        mainDispatcher: CoroutineDispatcher = Dispatchers.Main
    ) {
       lifecycleScope.launch(ioDispatcher) {
           val userType = async(ioDispatcher) {
               viewModel.takeTheUserType(viewModel.email)
           }
           userType.await()?.let { viewModel.setTypeOfUser(it) }
           if (viewModel.isSignedIn && userType.await() == "Yönetici") {
               withContext(mainDispatcher) {
                   goToAdminHomePageActivity()
               }
           }
           if (viewModel.isSignedIn && userType.await() == "Sakin") {
               withContext(mainDispatcher) {
                   goToResidentHomePageActivity()
               }
           }
       }
    }

}