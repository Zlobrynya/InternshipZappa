package com.zlobrynya.internshipzappa.fragment

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zlobrynya.internshipzappa.R
import com.zlobrynya.internshipzappa.activity.TableSelectActivity
import com.zlobrynya.internshipzappa.adapter.AdapterBookingButtons
import com.zlobrynya.internshipzappa.adapter.AdapterDays
import kotlinx.android.synthetic.main.fragment_booking.view.*
import java.util.*
import kotlin.collections.ArrayList

/**
 * Число дней, добавляемых к дате в календаре
 */
const val DAY_OFFSET: Int = 1

/**
 * Фрагмент брони(выбор даты и времени)
 */
class BookingFragment : Fragment(), AdapterDays.OnDateListener, AdapterBookingButtons.OnDurationListener,
    View.OnClickListener {

    /**
     * Реализация onClick
     * @param v Нажатый элемент
     */
    override fun onClick(v: View) {
        // Кнопка выбрать стол
        when (v.id) {
            R.id.book_button -> {
                val intent = Intent(activity, TableSelectActivity::class.java)
                startActivity(intent)
            }
        }
    }

    /**
     * Список дней для отображения
     */
    private val schedule: ArrayList<Date> = ArrayList()

    /**
     * Список с вариантами продоллжительности брони
     */
    private val booking: ArrayList<String> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.fragment_booking, container, false)

        initCalendar()
        initBookingDurationList()
        view = initCalendarRecycler(view)
        view = initDurationRecycler(view)

        view.book_button.setOnClickListener(this) // Установка обработчика для кнопки выбрать столк

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
    private fun initBookingDurationList() {
        booking.add("2 ч")
        booking.add("2 ч\n30мин")
        booking.add("3 ч")
        booking.add("3 ч\n30мин")
        booking.add("4 ч")
    }

    /**
     * Настраивает ресайклер для календаря
     */
    private fun initCalendarRecycler(view: View): View {
        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        view.days_recycler.layoutManager = layoutManager
        view.days_recycler.adapter = AdapterDays(schedule, this)
        return view
    }

    /**
     * Настраивает ресайклер для вариантов длительности бронирования
     */
    private fun initDurationRecycler(view: View): View {
        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        view.book_duration_recycler.layoutManager = layoutManager
        view.book_duration_recycler.adapter = AdapterBookingButtons(booking, this)
        return view
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