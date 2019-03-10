package com.zlobrynya.internshipzappa.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zlobrynya.internshipzappa.R
import com.zlobrynya.internshipzappa.adapter.AdapterBookingButtons
import com.zlobrynya.internshipzappa.adapter.AdapterDays
import kotlinx.android.synthetic.main.fragment_booking.view.*
import java.util.*
import kotlin.collections.ArrayList


/**
 * Фрагмент брони(выбор даты и времени)
 */
class BookingFragment : Fragment(), AdapterDays.OnDateListener, AdapterBookingButtons.OnDurationListener {

    /**
     * Список дней для отображения
     */
    private val schedule: ArrayList<Date> = ArrayList()

    /**
     * Список с вариантами продоллжительности брони
     */
    private val booking: ArrayList<String> = ArrayList()

    /**
     * Число дней, добавляемых к дате в календаре
     */
    val DAY_OFFSET: Int = 1


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_booking, container, false)

        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        val layoutManager2 = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)

        view.days_recycler.layoutManager = layoutManager
        view.book_duration_recycler.layoutManager = layoutManager2

        initCalendar()
        initBookingDurationButtons()

        view.days_recycler.adapter = AdapterDays(schedule, this)
        view.book_duration_recycler.adapter = AdapterBookingButtons(booking, this)

        return view
    }

    /**
     * Набивает данными список с датами
     */
    private fun initCalendar() {
        val calendar: Calendar = Calendar.getInstance()
        schedule.add(calendar.time) // Вне цикла добавим один элемент (т.е. текущий день) в список
        for (i in 0..5) {
            // В цикле добавим еще нужное число дней, увеличивая дату на 1 день (DAY_OFFSET)
            calendar.add(Calendar.DATE, DAY_OFFSET)
            schedule.add(calendar.time)
        }
    }

    /**
     * Набивает данными список с вариантами продоллжительности брони
     */
    private fun initBookingDurationButtons() {
        booking.add("2 ч")
        booking.add("2 ч\n30мин")
        booking.add("3 ч")
        booking.add("3 ч\n30мин")
        booking.add("4 ч")
    }

    /**
     * Реализация интерфейса для обработки нажатий на числа в ресайклере вне адаптера
     * @param position Позиция элемента
     */
    override fun onDateClick(position: Int) {
        // Может понадобится
        Log.d("TOPKEK", schedule[position].toString())
    }

    /**
     * Реализация интерфейса для обработки нажатий на продолжительность брони в ресайклере вне адаптера
     * @param position Позиция элемента
     */
    override fun onDurationClick(position: Int) {
        // Может понадобится
        Log.d("TOPKEK", booking[position])
    }
}