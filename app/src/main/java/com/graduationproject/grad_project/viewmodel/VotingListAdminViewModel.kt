package com.graduationproject.grad_project.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.graduationproject.grad_project.firebase.VotingOperations
import com.graduationproject.grad_project.model.Voting

class VotingListAdminViewModel: ViewModel() {

    private val _continuesVoting = MutableLiveData<MutableList<Voting?>>()
    val continuesVoting: LiveData<MutableList<Voting?>> get() = _continuesVoting

    private val _finishedVoting = MutableLiveData<MutableList<Voting?>>()
    val finishedVoting: LiveData<MutableList<Voting?>> get() = _finishedVoting

    fun retrieveFinishedVoting() {
        VotingOperations.retrieveFinishedVotingForAdmin(_finishedVoting)
    }
    fun retrieveContinuesVoting() {
        VotingOperations.retrieveContinuesVotingForAdmin(_continuesVoting)
    }

}