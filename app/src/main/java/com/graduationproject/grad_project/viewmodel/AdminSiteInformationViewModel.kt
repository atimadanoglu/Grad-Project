package com.graduationproject.grad_project.viewmodel

import androidx.lifecycle.ViewModel

class AdminSiteInformationViewModel: ViewModel() {

    private var _siteName = ""
    val siteName get() = _siteName

    private var _city = ""
    val city get() = _city

    private var _district = ""
    val district get() = _district

    private var _blockCount = 0
    val blockCount get() = _blockCount
}