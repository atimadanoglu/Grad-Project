package com.graduationproject.grad_project.view.admin.dialogs

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.os.Message
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import com.graduationproject.grad_project.R
import com.graduationproject.grad_project.viewmodel.AddVotingViewModel
import java.util.*


class DatePickerDialogFragment(private val viewModel: AddVotingViewModel)
    : DialogFragment(), DatePickerDialog.OnDateSetListener {

    private val calendar: Calendar by lazy {
        Calendar.getInstance()
    }
    // two weeks in milliseconds
    private val twoWeeks = 604800000L * 2
    private val oneDay = 604800000L / 7

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            val newDialog = DatePickerDialog(it, this, year, month, day)
            newDialog.create()
            newDialog.datePicker.minDate = calendar.timeInMillis + oneDay
            newDialog.datePicker.maxDate = calendar.timeInMillis + twoWeeks
            newDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setText(R.string.tamam)
            newDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setText(R.string.iptal)
            newDialog
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun onDateSet(p0: DatePicker?, year: Int, month: Int, day: Int) {
        viewModel.saveChosenDate("$day-${month + 1}-$year")
        calendar.set(year, month, day)
        viewModel.setDateLongValue(calendar.timeInMillis)
    }

}