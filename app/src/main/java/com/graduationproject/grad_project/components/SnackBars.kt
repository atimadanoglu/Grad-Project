package com.graduationproject.grad_project.components

import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.graduationproject.grad_project.R

object SnackBars {

    fun showWrongActivationCodeSnackBar(view: View?) {
        if (view != null) {
            Snackbar.make(
                view,
                R.string.yanlışAktivasyonKodu,
                Snackbar.LENGTH_LONG
            ).show()
        }
    }

    fun showEmptySpacesSnackBar(view: View?) {
        if (view != null) {
            Snackbar.make(
                view,
                R.string.boşluklarıDoldur,
                Snackbar.LENGTH_LONG
            ).show()
        }
    }

    fun showInvalidEmailAddressSnackBar(view: View?) {
        if (view != null) {
            Snackbar.make(
                view,
                R.string.geçersizEmailAdresi,
                Snackbar.LENGTH_LONG
            ).show()
        }
    }
}