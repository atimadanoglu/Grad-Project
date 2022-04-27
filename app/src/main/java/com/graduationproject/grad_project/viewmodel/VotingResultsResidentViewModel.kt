package com.graduationproject.grad_project.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.graduationproject.grad_project.firebase.VotingOperations
import com.graduationproject.grad_project.model.Voting

class VotingResultsResidentViewModel: ViewModel() {

    private val _voting = MutableLiveData<MutableList<Voting?>>()
    val voting: LiveData<MutableList<Voting?>> get() = _voting

    fun retrieveVoting() {
        VotingOperations.retrieveFinishedVotingForResident(_voting)
    }

}