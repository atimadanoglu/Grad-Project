package com.graduationproject.grad_project.model

data class Administrator(
    val fullName: String? = null,
    val email: String? = null,
    val phoneNumber: String? = null,
    val blockCount: String? = null,
    val player_id: String? = null,
    val flatCount: Long? = 0,
    val city: String? = null,
    val district: String? = null,
    val siteName: String? = null,
    val typeOfUser: String? = null,
    val expendituresAmount: Long? = 0
)