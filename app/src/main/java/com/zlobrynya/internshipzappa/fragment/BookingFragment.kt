package com.zlobrynya.internshipzappa.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.zlobrynya.internshipzappa.R
import com.zlobrynya.internshipzappa.adapter.AdapterDays
import kotlinx.android.synthetic.main.fragment_booking.view.*
import java.util.*


/**
 * Фрагмент брони(выбор даты и времени)
 */
class BookingFragment : Fragment(), AdapterDays.OnNoteListener {

    /**
     * Список дней для отображения
     */
    private val schedule: ArrayList<Date> = ArrayList()

    /**
     * Число дней, добавляемых к дате в календаре
     */
    val DAY_OFFSET: Int = 1


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_booking, container, false)
        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        view.days_recycler.layoutManager = layoutManager
        initRecycleView()
        view.days_recycler.adapter = AdapterDays(schedule, this)
        return view
    }

    private fun initRecycleView() {
        val calendar: Calendar = Calendar.getInstance()
        for (i in 0..7) {
            calendar.add(Calendar.DATE, DAY_OFFSET)
            schedule.add(calendar.time)
        }
    }

    /**
     * Реализация интерфейса для обработки нажатий на элементы ресайклер вью
     * @param position Позиция элемента
     */
    override fun onNoteClick(position: Int) {
        schedule[position]
        //Toast.makeText(context, "Нажал", Toast.LENGTH_SHORT).show()
    }
}