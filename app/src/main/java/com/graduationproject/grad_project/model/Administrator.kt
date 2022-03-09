package com.graduationproject.grad_project.model

class Administrator(
    override var fullName: String,
    override var email: String,
    override var password: String,
    override var phoneNumber: String,
    private var adminKey: String
) : User() {
    init {
        authorizationToAdminInterface = true
    }

}
