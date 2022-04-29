package com.graduationproject.grad_project.model

import com.google.firebase.Timestamp

data class Service(
    val id: String = "",
    val name: String = "",
    val phoneNumber: String = "",
    val type: String = "",
    val date: Timestamp? = null
)
