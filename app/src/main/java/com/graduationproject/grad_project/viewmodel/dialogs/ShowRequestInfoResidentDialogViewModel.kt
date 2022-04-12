package com.graduationproject.grad_project.viewmodel.dialogs

import androidx.lifecycle.ViewModel

class ShowRequestInfoResidentDialogViewModel: ViewModel() {

    private var _title = ""
    val title get() = _title
    private var _content = ""
    val content get() = _content
    private var _type = ""
    val type get() = _type

    fun setTitle(title: String) { _title = title }
    fun setContent(content: String) { _content = content }
    fun setType(type: String) { _type = type }

}