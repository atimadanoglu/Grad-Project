package com.graduationproject.grad_project.model

data class Site(
    val siteName : String = "",
    val city: String = "",
    val district: String = "",
    val blockCount: String = "",
    val flatCount : Long = 0,
    val expenditures: List<Expenditure?>? = null,
    val voting : List<Voting>? = null,
    val services : List<Service>? = null,
    val meetings : List<Meeting>? = null,
    val requests : List<Request>? = null,
    private var monthlyPayment: Long = 0
)
