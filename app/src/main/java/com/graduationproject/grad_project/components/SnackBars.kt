package com.graduationproject.grad_project.components

import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.graduationproject.grad_project.R

object SnackBars {

    /**
     * @param view --> It's the view that snackBar will be presented
     *
     * */
    fun showWrongActivationCodeSnackBar(view: View?) {
        if (view != null) {
            Snackbar.make(
                view,
                R.string.yanlışAktivasyonKodu,
                Snackbar.LENGTH_LONG
            ).show()
        }
    }

    /**
     * @param view --> It's the view that snackBar will be presented
     *
     * */
    fun showEmptySpacesSnackBar(view: View?) {
        if (view != null) {
            Snackbar.make(
                view,
                R.string.boşluklarıDoldur,
                Snackbar.LENGTH_LONG
            ).show()
        }
    }

    /**
     * @param view --> It's the view that snackBar will be presented
     *
     * */
    fun showInvalidEmailAddressSnackBar(view: View?) {
        if (view != null) {
            Snackbar.make(
                view,
                R.string.geçersizEmailAdresi,
                Snackbar.LENGTH_LONG
            ).show()
        }
    }

    fun showSiteInfoCouldNotWrittenSnackBar(view: View?) {
        if (view != null) {
            Snackbar.make(
                view,
                R.string.siteBilgisiYazdırılamadı,
                Snackbar.LENGTH_LONG
            ).show()
        }
    }

    fun showUserInfoCouldNotWritten(view: View?) {
        if (view != null) {
            Snackbar.make(
                view,
                R.string.kullanıcıVerisiYazdırılamadı,
                Snackbar.LENGTH_LONG
            ).show()
        }
    }
}