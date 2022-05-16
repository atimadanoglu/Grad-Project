package com.graduationproject.grad_project.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.graduationproject.grad_project.firebase.SiteOperations

class HomeFragmentViewModel: ViewModel() {

    private val _requestsCount = MutableLiveData<Long?>()
    val requestsCount: LiveData<Long?> get() = _requestsCount

    private val _totalCollectedMoney = MutableLiveData<Long?>()
    val totalCollectedMoney: LiveData<Long?> get() = _totalCollectedMoney

    private val _votingCount = MutableLiveData<Long?>()
    val votingCount: LiveData<Long?> get() = _votingCount

    fun retrieveTotalCollectedMoney() {
        SiteOperations.retrieveTotalPayment(_totalCollectedMoney)
    }

    fun retrieveTotalVotingMoney() {
        SiteOperations.retrieveTotalVotingCount(_votingCount)
    }

    fun retrieveRequestsCount() {
        SiteOperations.retrieveRequestsCount(_requestsCount)
    }
}
