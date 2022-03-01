package com.graduationproject.grad_project.model

class Administrator(
    override var fullName: String,
    override var email: String,
    override var password: String,
    override var phoneNumber: String,
    override var siteName: String,
    override var location: Location,
    private var adminKey : String
) : User() {
    init {
        authorizationToAdminInterface = true
    }

}
