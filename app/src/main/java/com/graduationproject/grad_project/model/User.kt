package com.graduationproject.grad_project.model

abstract class User {
    protected abstract var fullName : String
    protected abstract var email : String
    protected abstract var password : String
    protected abstract var phoneNumber : String
    protected abstract var siteName : String
    protected abstract var location: Location
    protected var authorizationToAdminInterface : Boolean = false
}