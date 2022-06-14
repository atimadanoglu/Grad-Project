package com.graduationproject.grad_project.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.graduationproject.grad_project.firebase.VotingOperations
import com.graduationproject.grad_project.model.Voting

class VoteResidentViewModel: ViewModel() {

    private val _continuesVoting = MutableLiveData<MutableList<Voting?>>()
    val continuesVoting: LiveData<MutableList<Voting?>> get() = _continuesVoting

    private val _clickedVoting = MutableLiveData<Voting>()
    val clickedVoting: LiveData<Voting> get() = _clickedVoting

    fun saveClickedVoting(value: Voting) {
        _clickedVoting.value = value
    }

    fun retrieveContinuesVoting() {
        VotingOperations.retrieveContinuesVotingForResident(_continuesVoting)
    }
}