package com.zlobrynya.internshipzappa.util

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.View
import com.zlobrynya.internshipzappa.R
import kotlinx.android.synthetic.main.layout_custom_time_picker_dialog.view.*

/**
 * Кастомный таймпикер
 */
class CustomTimePickerDialog : DialogFragment() {
    private lateinit var alertView: View
    private lateinit var builderAlertDialog: AlertDialog.Builder
    private var positiveClickListener: PositiveClickListener? = null

    private val minutesMinValue = 1
    private val minutesMaxValue = 2

    private val hoursMinValue = 1
    private val hoursMaxValue = 24


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        alertView = activity!!.layoutInflater.inflate(R.layout.layout_custom_time_picker_dialog, null)
        builderAlertDialog = AlertDialog.Builder(activity)
        initNumberPickers(alertView)
        setClickListeners(alertView)
        builderAlertDialog.setView(alertView)
        return builderAlertDialog.create()
    }

    private fun initNumberPickers(alertView: View) {
        alertView.numberPickerHours.minValue = hoursMinValue
        alertView.numberPickerHours.maxValue = hoursMaxValue
        alertView.numberPickerHours.displayedValues =
            arrayOf("00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23")

        alertView.numberPickerMinutes.minValue = minutesMinValue
        alertView.numberPickerMinutes.maxValue = minutesMaxValue
        alertView.numberPickerMinutes.displayedValues = arrayOf("00", "30")
    }

    private fun setClickListeners(alertView: View) {
        alertView.textViewCancel.setOnClickListener {
            dismiss()
        }

        alertView.textViewOk.setOnClickListener {
            positiveClickListener?.let { it ->
                val minutesIndex = alertView.numberPickerMinutes.value - 1
                val hoursIndex = alertView.numberPickerHours.value - 1

                val minutes = alertView.numberPickerMinutes.displayedValues[minutesIndex]
                val hours = alertView.numberPickerHours.displayedValues[hoursIndex]
                it.onClick(hours, minutes)
                dismiss()
            }
        }
    }

    fun setOnPositiveClickListener(listener: PositiveClickListener) {
        this.positiveClickListener = listener
    }

}