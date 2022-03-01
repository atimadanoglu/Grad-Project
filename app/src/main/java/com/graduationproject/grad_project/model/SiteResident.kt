package com.graduationproject.grad_project.model

class SiteResident(
    override var fullName: String,
    override var email: String,
    override var password: String,
    override var phoneNumber: String,
    override var siteName: String,
    override var location: Location,
    private var blockNo : Int,
    private var flatNo : Int
) : User() {

}
