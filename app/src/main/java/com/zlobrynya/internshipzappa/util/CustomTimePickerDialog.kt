package com.zlobrynya.internshipzappa.util

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.util.Log
import android.view.View
import android.widget.NumberPicker
import com.zlobrynya.internshipzappa.R
import kotlinx.android.synthetic.main.layout_custom_time_picker_dialog.view.*
import java.util.*

/**
 * Кастомный таймпикер
 */
class CustomTimePickerDialog : DialogFragment(), NumberPicker.OnValueChangeListener {

    /**
     * Час открытия ресторана
     */
    private var timeOpen: Int = -1

    /**
     * Час закрытия ресторана
     */
    private var timeClose: Int = -1

    /**
     * Массив с часами в сутках
     */
    private val scheduleArray = arrayOf(
        "00",
        "01",
        "02",
        "03",
        "04",
        "05",
        "06",
        "07",
        "08",
        "09",
        "10",
        "11",
        "12",
        "13",
        "14",
        "15",
        "16",
        "17",
        "18",
        "19",
        "20",
        "21",
        "22",
        "23"
    )

    private lateinit var alertView: View
    private lateinit var builderAlertDialog: AlertDialog.Builder
    private var positiveClickListener: PositiveClickListener? = null

    private val minutesMinValue = 1
    private val minutesMaxValue = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parseTime()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        alertView = activity!!.layoutInflater.inflate(R.layout.layout_custom_time_picker_dialog, null)
        builderAlertDialog = AlertDialog.Builder(activity)
        initNumberPickers(alertView)
        setClickListeners(alertView)
        builderAlertDialog.setView(alertView)
        alertView.numberPickerHours.setOnValueChangedListener(this)
        return builderAlertDialog.create()
    }

    /**
     * Парсит строки с временем начала\конца работы ресторана
     * В целочисленные переменные устанавливает час начала работы и час конца
     */
    private fun parseTime() {
        val open = arguments!!.getString("time_open") // Заберем из аргументов строки
        val close = arguments!!.getString("time_close")
        //Log.d("TOPKEK", "Время открытия ресторана $open")
        //Log.d("TOPKEK", "Время закрытия ресторана $close")

        var scanner = Scanner(open) // Используем сканнер
        var timeScanner = Scanner(scanner.next())
        timeScanner.useDelimiter(":") // В качестве разделителя будет двоеточие
        timeOpen = timeScanner.nextInt() // Берем первое число до разделителя
        //Log.d("TOPKEK", "Час открытия ресторана $timeOpen")

        scanner = Scanner(close) // То же самое для второй строки
        timeScanner = Scanner(scanner.next())
        timeScanner.useDelimiter(":")
        timeClose = timeScanner.nextInt()
        //Log.d("TOPKEK", "Час закрытия ресторана ${timeClose}")

        val calendar: Calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, timeClose)
        calendar.add(Calendar.HOUR_OF_DAY, -2)
        timeClose = calendar.get(Calendar.HOUR_OF_DAY)
    }

    /**
     * Инициализирует данными намбер пикеры
     */

    private fun initNumberPickers(alertView: View) {

        val availableSchedule = Arrays.copyOfRange(scheduleArray, timeOpen, timeClose + 1)
        //Log.d("TOPKEK", "Доступные часы для брони ${Arrays.toString(availableSchedule)}")

        alertView.numberPickerHours.minValue = timeOpen
        alertView.numberPickerHours.maxValue = timeClose
        alertView.numberPickerHours.displayedValues = availableSchedule

        if (availableSchedule.size == 1) {
            alertView.numberPickerMinutes.minValue = minutesMinValue
            alertView.numberPickerMinutes.maxValue = minutesMinValue
        } else {
            alertView.numberPickerMinutes.minValue = minutesMinValue
            alertView.numberPickerMinutes.maxValue = minutesMaxValue
        }
        alertView.numberPickerMinutes.displayedValues = arrayOf("00", "30")
    }

    private fun setClickListeners(alertView: View) {
        alertView.textViewCancel.setOnClickListener {
            dismiss()
        }

        alertView.textViewOk.setOnClickListener {
            positiveClickListener?.let { it ->
                val minutesIndex = alertView.numberPickerMinutes.value - 1
                val hoursIndex = alertView.numberPickerHours.value
                //Log.d("TOPKEK", "Выбранный час брони $hoursIndex")

                val minutes = alertView.numberPickerMinutes.displayedValues[minutesIndex]
                val hours = scheduleArray[hoursIndex]
                it.onClick(hours, minutes)
                dismiss()
            }
        }
    }

    fun setOnPositiveClickListener(listener: PositiveClickListener) {
        this.positiveClickListener = listener
    }

    /**
     * Обработчик изменений в намбер пикере
     * @param oldVal Старое значение
     * @param newVal Новое значение
     */
    override fun onValueChange(picker: NumberPicker?, oldVal: Int, newVal: Int) {
        //Log.d("KEKLOL", "Было $oldVal Стало $newVal")
        if (newVal == timeClose) alertView.numberPickerMinutes.maxValue = minutesMinValue
        else alertView.numberPickerMinutes.maxValue = minutesMaxValue
    }

}