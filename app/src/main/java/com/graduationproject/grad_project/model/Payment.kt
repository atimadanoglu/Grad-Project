package com.graduationproject.grad_project.model

import com.google.firebase.Timestamp

data class Payment(
    val id: String = "",
    val thePersonNameWhoPaid: String = "",
    val thePersonBlockNameWhoPaid: String = "",
    val thePersonFlatNoWhoPaid: Long = 0,
    val paidAmount: Long = 0,
    val date: Timestamp? = null
)