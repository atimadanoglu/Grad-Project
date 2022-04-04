package com.graduationproject.grad_project.model

import com.google.firebase.Timestamp

data class Notification(
    val title: String,
    val message: String,
    val pictureUri: String,
    val id: String,
    val date: Timestamp
)
