package com.graduationproject.grad_project.model

data class Expenditure(
    val id: String,
    val title: String,
    val content: String,
    val amount: Int,
    val documentUri: String
)
