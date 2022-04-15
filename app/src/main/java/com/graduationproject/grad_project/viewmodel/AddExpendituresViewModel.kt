package com.graduationproject.grad_project.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.graduationproject.grad_project.firebase.StorageOperations
import kotlinx.coroutines.launch

class AddExpendituresViewModel: ViewModel() {

    private val _title = MutableLiveData("")
    val title: LiveData<String> get() = _title
    private val _content = MutableLiveData("")
    val content: LiveData<String> get() = _content
    private val _amount = MutableLiveData(0)
    val amount: LiveData<Int> get() = _amount
    private val _uri = MutableLiveData<Uri>()
    val uri: LiveData<Uri> get() = _uri


    fun uploadDocument(uri: Uri) {
        viewModelScope.launch {
            _uri.value = uri
            StorageOperations.uploadImage(uri)
        }
    }


}