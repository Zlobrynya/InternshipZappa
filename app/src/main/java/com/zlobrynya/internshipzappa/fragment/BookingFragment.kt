package com.zlobrynya.internshipzappa.fragment

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zlobrynya.internshipzappa.activity.booking.TableSelectActivity
import com.zlobrynya.internshipzappa.adapter.booking.AdapterBookingButtons
import com.zlobrynya.internshipzappa.adapter.booking.AdapterDays
import kotlinx.android.synthetic.main.fragment_booking.view.*
import java.util.*
import kotlin.collections.ArrayList
import android.text.format.DateUtils
import com.zlobrynya.internshipzappa.R
import com.zlobrynya.internshipzappa.tools.database.VisitingHoursDB
import com.zlobrynya.internshipzappa.tools.retrofit.DTOs.bookingDTOs.visitingHoursDTO
import com.zlobrynya.internshipzappa.util.CustomTimePickerDialog
import com.zlobrynya.internshipzappa.util.PositiveClickListener
import kotlinx.android.synthetic.main.fragment_booking.*
import java.text.SimpleDateFormat


/**
 * Число дней, добавляемых к дате в календаре
 */
const val DAY_OFFSET: Int = 1

/**
 * Фрагмент брони(выбор даты и времени)
 */
class BookingFragment : Fragment(), AdapterDays.OnDateListener, AdapterBookingButtons.OnDurationListener,
    PositiveClickListener {

    /**
     * Кастомный таймпикер
     */
    private val timePickerDialog = CustomTimePickerDialog()

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
    private var bookTimeAndDate: Date? = null

    /**
     * Экземпляр календаря для работы таймпикера
     */
    private var calendar: Calendar = Calendar.getInstance()

    /**
     * Время открытия ресторана
     */
    private var timeOpen: String = ""

    /**
     * Время закрытия ресторана
     */
    private var timeClose: String = ""

    /**
     * Выбранная длительность брони (позиция элемента в ресайклере)
     */
    private var selectedDuration: Int = 0

    /**
     * Объект базы данных для получения графика
     */
    private lateinit var dataBase: VisitingHoursDB

    /**
     * График работы ресторана
     */
    lateinit var timeTable: ArrayList<visitingHoursDTO>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        bookingView = inflater.inflate(R.layout.fragment_booking, container, false)

        dataBase = VisitingHoursDB(context!!) // Инициализация БД
        timeTable = dataBase.getVisitingHours() // Получение графика работы ресторана

        initCalendar()
        updateSchedule()
        initBookingDurationList()
        initCalendarRecycler()
        initDurationRecycler()

        timePickerDialog.setOnPositiveClickListener(this) // Установка обработчика для позитивной кнопки таймпикера
        bookingView.book_button.setOnClickListener(onClickListener) // Установка обработчика для кнопки выбрать столик
        bookingView.book_time_select.setOnClickListener(onClickListener) // Установка обработчика для поля время

        return bookingView
    }

    /**
     * Обновляет переменные timeOpen и timeClose в соответствии с выбранной датой
     */
    private fun updateSchedule() {
        val dayOfWeekFormat = SimpleDateFormat("E") // Шаблон для вывода дня недели
        val dayOfWeek = dayOfWeekFormat.format(calendar.time)
        for (i in 0 until timeTable.size) {
            if (timeTable[i].week_day.toLowerCase() == dayOfWeek.toLowerCase()) {
                Log.d("TOPKEK", "Сейчас $dayOfWeek")
                timeOpen = timeTable[i].time_from
                timeClose = timeTable[i].time_to
                break
            }
        }

        bookTimeAndDate = null // Сбросим выбранное время
    }

    /**
     * Обработчик нажатий
     */
    private val onClickListener = View.OnClickListener {
        when (it.id) {
            // Кнопка выбрать стол
            R.id.book_button -> {
                if (bookTimeAndDate != null) { // Проверим, выбрал ли пользователь время
                    bookingView.book_time_select_label.error = null // Скроем варнинг

                    openTableList()
                } else bookingView.book_time_select_label.error = "Выберите время" // Выведем варнинг
            }
            // Поле "Выберите время"
            R.id.book_time_select -> {
                showTimePickerDialog()
            }
        }
    }

    /**
     * Открывает таймпикер
     */
    private fun showTimePickerDialog() {
        val args = Bundle() // Аргументы для таймпикера
        args.putString("time_open", timeOpen)
        args.putString("time_close", timeClose)
        timePickerDialog.arguments = args
        timePickerDialog.show(fragmentManager, null)
    }

    /**
     * Обработчик нажатий на позитивную кнопку в таймпикере
     * @param hours Часы, выбранные в таймпикере
     * @param minutes Минуты, выбранные в таймпикере
     */
    override fun onClick(hours: String, minutes: String) {
        book_time_select.text = "$hours:$minutes"
        calendar.set(Calendar.HOUR_OF_DAY, hours.toInt())
        calendar.set(Calendar.MINUTE, minutes.toInt())
        setInitialDateTime()
    }

    /**
     * Открывает список доступных столов
     */
    private fun openTableList() {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd") // Форматирование для даты
        val timeFormat = SimpleDateFormat("HH:mm:ss") // Форматирование для времени

        val intent = Intent(activity, TableSelectActivity::class.java)
        intent.putExtra("book_date_begin", dateFormat.format(calendar.timeInMillis)) // Заполняем дату брони
        intent.putExtra("book_time_begin", timeFormat.format(calendar.timeInMillis))// Заполняем время начала брони
        intent.putExtra("book_date_end", dateFormat.format(calendar.timeInMillis)) // Заполняем дату брони
        when (selectedDuration) { // И время конца
            // 2 часа
            0 -> {
                if (timeFormat.format(calendar.timeInMillis) > timeFormat.format(calendar.timeInMillis + 2 * 60 * 60 * 1000)) {
                    intent.putExtra("book_date_end", dateFormat.format(calendar.timeInMillis + 2 * 60 * 60 * 1000))
                }
                intent.putExtra( // Заполняем время конца брони
                    "book_time_end",
                    timeFormat.format(calendar.timeInMillis + 2 * 60 * 60 * 1000)
                )
            }
            // 2 часа 30 минут
            1 -> {
                if (timeFormat.format(calendar.timeInMillis) > timeFormat.format(calendar.timeInMillis + 2 * 60 * 60 * 1000 + 30 * 60 * 1000)) {
                    intent.putExtra(
                        "book_date_end",
                        dateFormat.format(calendar.timeInMillis + 2 * 60 * 60 * 1000 + 30 * 60 * 1000)
                    )
                }
                intent.putExtra( // Заполняем время конца брони
                    "book_time_end",
                    timeFormat.format(calendar.timeInMillis + 2 * 60 * 60 * 1000 + 30 * 60 * 1000)
                )
            }
            // 3 часа
            2 -> {
                if (timeFormat.format(calendar.timeInMillis) > timeFormat.format(calendar.timeInMillis + 3 * 60 * 60 * 1000)) {
                    intent.putExtra("book_date_end", dateFormat.format(calendar.timeInMillis + 3 * 60 * 60 * 1000))
                }
                intent.putExtra( // Заполняем время конца брони
                    "book_time_end",
                    timeFormat.format(calendar.timeInMillis + 3 * 60 * 60 * 1000)
                )
            }
            // 3 часа 30 минут
            3 -> {
                if (timeFormat.format(calendar.timeInMillis) > timeFormat.format(calendar.timeInMillis + 3 * 60 * 60 * 1000 + 30 * 60 * 1000)) {
                    intent.putExtra(
                        "book_date_end",
                        dateFormat.format(calendar.timeInMillis + 3 * 60 * 60 * 1000 + 30 * 60 * 1000)
                    )
                }
                intent.putExtra( // Заполняем время конца брони
                    "book_time_end",
                    timeFormat.format(calendar.timeInMillis + 3 * 60 * 60 * 1000 + 30 * 60 * 1000)
                )
            }
            // 4 часа
            4 -> {
                if (timeFormat.format(calendar.timeInMillis) > timeFormat.format(calendar.timeInMillis + 4 * 60 * 60 * 1000)) {
                    intent.putExtra("book_date_end", dateFormat.format(calendar.timeInMillis + 4 * 60 * 60 * 1000))
                }
                intent.putExtra( // Заполняем время конца брони
                    "book_time_end",
                    timeFormat.format(calendar.timeInMillis + 4 * 60 * 60 * 1000)
                )
            }
        }

        startActivity(intent)
    }

    /**
     * Набивает данными список с датами
     */
    private fun initCalendar() {
        this.calendar = Calendar.getInstance() // Обновим глобальную переменную с календарем
        schedule.clear()
        val localCalendar: Calendar = Calendar.getInstance() // Локальная переменная с календарем для заполнения списков

        if (checkToday()) {
            schedule.add(localCalendar.time) // Вне цикла добавим один элемент (т.е. текущий день) в список
            for (i in 0..5) {
                // В цикле добавим еще нужное число дней, увеличивая дату на 1 день (DAY_OFFSET)
                localCalendar.add(Calendar.DATE, DAY_OFFSET)
                schedule.add(localCalendar.time)
            }
        } else { // Вне цикла в глобальном календаре увеличим дату на 1 день
            this.calendar.add(Calendar.DATE, DAY_OFFSET)
            for (i in 0..6) {
                // В цикле добавим еще нужное число дней, увеличивая дату на 1 день (DAY_OFFSET)
                localCalendar.add(Calendar.DATE, DAY_OFFSET)
                schedule.add(localCalendar.time)
            }
        }
    }

    /**
     * Проверяет, есть ли еще время на текущий день для брони
     * @return true, если до закрытия ресторана 5+ часов, иначе - false
     * 5 часов потому что отсечка 2 часа до закрытия ресторана + 3 часа отсечка до ближайшей брони
     */
    private fun checkToday(): Boolean {
        updateSchedule()
        val calendar: Calendar = Calendar.getInstance()
        val timeNow = calendar.timeInMillis
        val timeClose: Long = SimpleDateFormat("HH:mm:ss").parse(timeClose).time
        return timeClose - timeNow > 1000 * 60 * 60 * (2 + 3)
    }

    /**
     * Набивает данными список с вариантами продоллжительности брони
     */
    private fun initBookingDurationList() {
        booking.clear()
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
        bookingView.book_duration_recycler.adapter =
            AdapterBookingButtons(booking, this)
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
        updateSchedule() // Обновим расписание
        book_time_select.text = "Время" // Обновим значение во вьюшке
    }

    /**
     * Реализация интерфейса для обработки нажатий на продолжительность брони в ресайклере вне адаптера
     * @param position Позиция элемента
     */
    override fun onDurationClick(position: Int) {
        selectedDuration = position
    }
}