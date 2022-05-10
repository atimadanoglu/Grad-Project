package com.graduationproject.grad_project.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.graduationproject.grad_project.firebase.VotingOperations
import com.graduationproject.grad_project.model.Voting
import kotlinx.coroutines.launch
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class AddVotingViewModel: ViewModel() {

    companion object {
        const val TAG = "AddVotingViewModel"
    }

    private val _title = MutableLiveData("")
    val title: LiveData<String> get() = _title
    private val _content = MutableLiveData("")
    val content: LiveData<String> get() = _content
    private val _dateLongValue = MutableLiveData(0L)
    private val _chosenDate = MutableLiveData<String?>()
    val chosenDate: LiveData<String?> get() = _chosenDate

    fun setDateLongValue(value: Long) { _dateLongValue.value = value }

    fun saveVotingIntoDB(title: String, content: String) {
        viewModelScope.launch {
            setValues(title, content)
            if (checkTheValuesAreNotNull()) {
                val uuid = UUID.randomUUID()
                val voting = Voting(
                    uuid.toString(),
                    _title.value!!,
                    _content.value!!,
                    _dateLongValue.value!!
                )
                VotingOperations.saveVotingIntoDB(voting)
            }
        }
    }

    fun setValues(title: String, content: String) {
        _title.value = title
        _content.value = content
    }

    fun saveChosenDate(chosenDate: String) {
        _chosenDate.value = chosenDate
    }

    private fun checkTheValuesAreNotNull(): Boolean {
        return !_title.value.isNullOrEmpty() &&
                !_content.value.isNullOrEmpty() && !_chosenDate.value.isNullOrEmpty()
    }
}