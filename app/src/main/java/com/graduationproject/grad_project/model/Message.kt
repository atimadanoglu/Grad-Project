package com.graduationproject.grad_project.model

import com.google.firebase.Timestamp

data class Message(
    val title: String,
    val content: String,
    val id: String,
    val date: Timestamp
)