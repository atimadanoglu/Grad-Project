package com.graduationproject.grad_project.model

data class SiteResident(
    val fullName: String,
    val email: String,
    val password: String,
    val phoneNumber: String,
    val blockNo : String,
    val flatNo : Int,
    var debt : Double,
    val playerID : String
)
