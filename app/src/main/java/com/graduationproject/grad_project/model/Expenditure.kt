package com.graduationproject.grad_project.model

import com.google.firebase.Timestamp

data class Expenditure(
    val id: String,
    val title: String,
    val content: String,
    val amount: Int,
    val documentUri: String,
    val date: Timestamp
)
