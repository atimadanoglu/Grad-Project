package com.graduationproject.grad_project.viewmodel

import androidx.lifecycle.ViewModel

class ResidentSiteInformationViewModel: ViewModel() {

    private var _siteName = ""
    val siteName get() = _siteName

    private var _city = ""
    val city get() = _city

    private var _district = ""
    val district get() = _district

    private var _blockNo = ""
    val blockNo get() = _blockNo

    private var _flatNo = 0
    val flatNo get() = _flatNo
}