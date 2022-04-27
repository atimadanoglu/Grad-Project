package com.graduationproject.grad_project.model

data class Voting(
    val id: String = "",
    val title: String = "",
    val content: String = "",
    val date: Long = 0,
    var totalYes: Long = 0,
    var totalNo: Long = 0,
    val isFinished: Boolean = false,
    val residentsWhoVoted: MutableList<ResidentsWhoVoted?> = mutableListOf()
)
