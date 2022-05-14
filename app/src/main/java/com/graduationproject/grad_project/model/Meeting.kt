package com.graduationproject.grad_project.model

import com.google.firebase.Timestamp

data class Meeting(
    val id: String = "",
    val title: String = "",
    val hour: Long = 0,
    val minute: Long = 0,
    val date: Timestamp? = null
)
