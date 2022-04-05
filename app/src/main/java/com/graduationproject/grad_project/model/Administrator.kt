package com.graduationproject.grad_project.model

data class Administrator(
    var fullName: String? = null,
    var email: String? = null,
    var password: String? = null,
    var phoneNumber: String? = null,
    var blockCount: String? = null,
    var flatCount: Int? = null,
    var city: String? = null,
    var district: String? = null,
    var siteName: String? = null,
    var typeOfUser: String? = null,
    var uid: String? = null
)