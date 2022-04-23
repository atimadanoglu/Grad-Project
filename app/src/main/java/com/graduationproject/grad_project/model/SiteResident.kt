package com.graduationproject.grad_project.model

data class SiteResident(
    val fullName: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val blockNo : String = "",
    val flatNo : Long = 0,
    var debt : Long = 0,
    val player_id : String = "",
    val typeOfUser: String = "",
    val siteName: String = "",
    val city: String = "",
    val district: String = ""
)
