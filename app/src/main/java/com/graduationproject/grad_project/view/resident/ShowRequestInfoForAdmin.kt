package com.graduationproject.grad_project.view.resident

import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.graduationproject.grad_project.R
import com.graduationproject.grad_project.databinding.ShowRequestInfoForResidentBinding

class ShowRequestInfoForAdmin: AppCompatActivity() {

    private lateinit var binding: ShowRequestInfoForResidentBinding

    override fun onResume() {
        super.onResume()
        val typeList = resources.getStringArray(R.array.type_list)
        val arrayAdapter = ArrayAdapter(this, R.layout.request_dropdown_item, typeList)
        binding.requestTypeText.setAdapter(arrayAdapter)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ShowRequestInfoForResidentBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}