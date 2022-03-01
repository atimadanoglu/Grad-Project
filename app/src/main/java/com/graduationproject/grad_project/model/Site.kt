package com.graduationproject.grad_project.model

data class Site(private var name : String = "",
                private val blockCount : Int = 0,
                private val flatCount : Int = 0,
                private var administrator : Administrator? = null,
                private var siteResidents : ArrayList<SiteResident>? = null,
                private var location : Location? = null,
                private var income : ArrayList<Debt>? = null,
                private var announcements : ArrayList<Announcement>? = null,
                private var expenditures : ArrayList<Expenditure>? = null,
                private var voting : ArrayList<Voting>? = null,
                private var services : ArrayList<Service>? = null,
                private var meetings : ArrayList<Meeting>? = null,
                private var requests : ArrayList<Request>? = null,
                )
