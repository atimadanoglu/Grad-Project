package com.graduationproject.grad_project.model

import com.google.firebase.Timestamp

data class Request(
    val id: String,
    val title: String,
    val content: String,
    val type: String,
    val sentBy: String,
    val date: Timestamp
)
