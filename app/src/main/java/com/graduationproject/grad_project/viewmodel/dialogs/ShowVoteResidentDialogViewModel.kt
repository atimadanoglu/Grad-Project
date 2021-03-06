package com.graduationproject.grad_project.viewmodel.dialogs

import androidx.lifecycle.ViewModel
import com.graduationproject.grad_project.firebase.VotingOperations
import com.graduationproject.grad_project.model.Voting

class ShowVoteResidentDialogViewModel: ViewModel() {

    fun acceptButtonClicked(voting: Voting) {
        VotingOperations.acceptButtonClicked(voting)
        VotingOperations.saveResidentWhoVoted(voting, true)
    }

    fun rejectButtonClicked(voting: Voting) {
        VotingOperations.rejectButtonClicked(voting)
        VotingOperations.saveResidentWhoVoted(voting, false)
    }

}