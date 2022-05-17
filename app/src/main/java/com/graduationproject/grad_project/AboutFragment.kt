package com.graduationproject.grad_project

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.graduationproject.grad_project.databinding.FragmentAboutBinding

class AboutFragment : Fragment() {

    private var _binding: FragmentAboutBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentAboutBinding.inflate(inflater, container, false)
        binding.referenceFirst.setOnClickListener {
            goToFlaticonHomePage()
        }
        binding.referenceSecond.setOnClickListener {
            goToFlaticonAuthorsPage()
        }
        binding.referenceThird.setOnClickListener {
            goToIcon8Page()
        }
        return binding.root
    }

    private fun goToFlaticonHomePage() {
        val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.flaticon.com/"))
        startActivity(webIntent)
    }

    private fun goToFlaticonAuthorsPage() {
        val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.flaticon.com/authors/freepik"))
        startActivity(webIntent)
    }
    private fun goToIcon8Page() {
        val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://icons8.com/"))
        startActivity(webIntent)
    }
}