package com.graduationproject.grad_project.components

import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.graduationproject.grad_project.R

object SnackBars {

    fun showWrongActivationCodeSnackBar(view: View) {
        Snackbar.make(
            view,
            R.string.yanlışAktivasyonKodu,
            Snackbar.LENGTH_LONG
        ).show()
    }

    fun showEmptySpacesSnackBar(view: View) {
        Snackbar.make(
            view,
            R.string.boşluklarıDoldur,
            Snackbar.LENGTH_LONG
        ).show()
    }
}