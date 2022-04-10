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

    fun showDeletingDebtOperationCancelled(view: View?) {
        view?.let {
            Snackbar.make(
                view,
                R.string.borçSilmeİptalEdildi,
                Snackbar.LENGTH_LONG
            ).show()
        }
    }

    fun showAddingDebtOperationCancelled(view: View?) {
        view?.let {
            Snackbar.make(
                view,
                R.string.borçEklemeİptalEdildi,
                Snackbar.LENGTH_LONG
            ).show()
        }
    }

    fun showDeletingDebtOperationIsSuccessful(view: View?) {
        view?.let {
            Snackbar.make(
                view,
                R.string.borçSilmeBaşarılı,
                Snackbar.LENGTH_LONG
            ).show()
        }
    }

    fun showAddingDebtOperationIsSuccessful(view: View?) {
        view?.let {
            Snackbar.make(
                view,
                R.string.borçEklemeBaşarılı,
                Snackbar.LENGTH_LONG
            ).show()
        }
    }

    fun showAddingDebtOperationIsFailed(view: View?) {
        view?.let {
            Snackbar.make(
                view,
                R.string.borçEklemeBaşarısız,
                Snackbar.LENGTH_LONG
            ).show()
        }
    }
    fun showDeletingDebtOperationIsFailed(view: View?) {
        view?.let {
            Snackbar.make(
                view,
                R.string.borçSilmeBaşarısız,
                Snackbar.LENGTH_LONG
            ).show()
        }
    }
}