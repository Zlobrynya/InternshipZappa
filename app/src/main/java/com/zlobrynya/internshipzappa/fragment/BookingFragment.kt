package com.zlobrynya.internshipzappa.fragment

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zlobrynya.internshipzappa.activity.TableSelectActivity
import com.zlobrynya.internshipzappa.adapter.AdapterBookingButtons
import com.zlobrynya.internshipzappa.adapter.AdapterDays
import kotlinx.android.synthetic.main.fragment_booking.view.*
import java.util.*
import kotlin.collections.ArrayList
import android.app.TimePickerDialog
import android.text.format.DateUtils
import com.zlobrynya.internshipzappa.R


/**
 * Число дней, добавляемых к дате в календаре
 */
const val DAY_OFFSET: Int = 1

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
     * Вьюшка для фрагмента
     */
    private lateinit var bookingView: View

    /**
     * Время и дата, которое выбрал пользователь
     */
    private lateinit var bookTimeAndDate: Date

    /**
     * Экземпляр календаря для работы таймпикера
     */
    private val calendar: Calendar = Calendar.getInstance()

    /**
     * Выбранная длительность брони (позиция элемента в ресайклере)
     */
    private var selectedDuration: Int = 0


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        bookingView = inflater.inflate(R.layout.fragment_booking, container, false)

        initCalendar()
        initBookingDurationList()
        initCalendarRecycler()
        initDurationRecycler()

        bookingView.book_button.setOnClickListener(onClickListener) // Установка обработчика для кнопки выбрать столк
        bookingView.book_time_select.setOnClickListener(onClickListener) // Установка обработчика для поля время


        return bookingView
    }

    /**
     * Обработчик нажатий
     */
    private val onClickListener = View.OnClickListener {
        when (it.id) {
            // Кнопка выбрать стол
            R.id.book_button -> {
                if (::bookTimeAndDate.isInitialized) { // Проверим, выбрал ли пользователь время
                    bookingView.book_time_select_label.error = null // Скроем варнинг
                    openTableList()
                } else bookingView.book_time_select_label.error = "Выберите время" // Выведем варнинг
            }
            R.id.book_time_select -> {
                setTime()
            }
        }
    }

    /**
     * Открывает список доступных столов
     */
    private fun openTableList() {
        val intent = Intent(activity, TableSelectActivity::class.java)
        intent.putExtra("book_time_begin", calendar.timeInMillis) // В экстра положим время начала брони
        when (selectedDuration) { // И время конца
            // 2 часа
            0 -> {
                val date = Date(calendar.timeInMillis + 2 * 60 * 60 * 1000)
                intent.putExtra("book_time_end", date.time)
                Log.d("TOPKEK", "${calendar.time}, $date")
            }
            // 2 часа 30 минут
            1 -> {
                val date = Date(calendar.timeInMillis + 2 * 60 * 60 * 1000 + 30 * 60 * 1000)
                intent.putExtra("book_time_end", date.time)
                Log.d("TOPKEK", "${calendar.time}, $date")
            }
            // 3 часа
            2 -> {
                val date = Date(calendar.timeInMillis + 3 * 60 * 60 * 1000)
                intent.putExtra("book_time_end", date.time)
                Log.d("TOPKEK", "${calendar.time}, $date")
            }
            // 3 часа 30 минут
            3 -> {
                val date = Date(calendar.timeInMillis + 3 * 60 * 60 * 1000 + 30 * 60 * 1000)
                intent.putExtra("book_time_end", date.time)
                Log.d("TOPKEK", "${calendar.time}, $date")
            }
            // 4 часа
            4 -> {
                val date = Date(calendar.timeInMillis + 4 * 60 * 60 * 1000)
                intent.putExtra("book_time_end", date.time)
                Log.d("TOPKEK", "${calendar.time}, $date")
            }
        }
        startActivity(intent)
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
    private fun initCalendarRecycler() {
        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        bookingView.days_recycler.layoutManager = layoutManager
        bookingView.days_recycler.adapter = AdapterDays(schedule, this)
    }

    /**
     * Настраивает ресайклер для вариантов длительности бронирования
     */
    private fun initDurationRecycler() {
        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        bookingView.book_duration_recycler.layoutManager = layoutManager
        bookingView.book_duration_recycler.adapter = AdapterBookingButtons(booking, this)
    }

    /**
     * Собирает и открывает диалоговое окно с выбором времени
     */
    private fun setTime() {
        val dialog = TimePickerDialog(
            activity,
            R.style.TimePickerTheme, // Кастомный стиль
            timeSetListener, // Обработчик
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true // 24-часовой формат
        )
        dialog.show()
    }

    /**
     * Обработчик на выбор времени в таймпикере
     */
    private val timeSetListener: TimePickerDialog.OnTimeSetListener =
        TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            calendar.set(Calendar.MINUTE, minute)
            setInitialDateTime()
        }

    /**
     * Выводит выбранное время в текстовое поле
     */
    private fun setInitialDateTime() {
        bookingView.book_time_select.text = DateUtils.formatDateTime(
            activity,
            calendar.timeInMillis,
            DateUtils.FORMAT_SHOW_TIME
        )
        bookTimeAndDate = calendar.time // Запишем выбранное время
        Log.d(
            "TOPKEK",
            DateUtils.formatDateTime(
                activity,
                calendar.timeInMillis,
                DateUtils.FORMAT_SHOW_DATE or DateUtils.FORMAT_SHOW_TIME
            )
        )
    }

    /**
     * Реализация интерфейса для обработки нажатий на числа в ресайклере вне адаптера
     * @param position Позиция элемента
     */
    override fun onDateClick(position: Int) {
        calendar.set(Calendar.DATE, schedule[position].date) // Установим в календарь выбранную дату
        Log.d(
            "TOPKEK",
            DateUtils.formatDateTime(
                activity,
                calendar.timeInMillis,
                DateUtils.FORMAT_SHOW_DATE or DateUtils.FORMAT_SHOW_TIME
            )
        )
    }

    /**
     * Реализация интерфейса для обработки нажатий на продолжительность брони в ресайклере вне адаптера
     * @param position Позиция элемента
     */
    override fun onDurationClick(position: Int) {
        selectedDuration = position
    }
}