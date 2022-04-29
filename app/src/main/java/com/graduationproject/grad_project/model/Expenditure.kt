package com.graduationproject.grad_project.model

import com.google.firebase.Timestamp

data class Expenditure(
    val id: String = "",
    val title: String = "",
    val content: String = "",
    val amount: Long = 0,
    val documentUri: String = "",
    val date: Timestamp? = null
)
