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
import com.zlobrynya.internshipzappa.adapter.booking.AdapterBookingDuration
import com.zlobrynya.internshipzappa.adapter.booking.AdapterDays
import kotlinx.android.synthetic.main.fragment_booking.view.*
import java.util.*
import kotlin.collections.ArrayList
import android.text.format.DateUtils
import android.widget.Toast
import com.zlobrynya.internshipzappa.R
import com.zlobrynya.internshipzappa.adapter.booking.BookDuration
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
 * 2 часа в миллисекундах
 */
const val TWO_HOURS: Long = 2 * 60 * 60 * 1000

/**
 * 2 с половиной часа в миллисекундах
 */
const val TWO_AND_HALF_HOURS: Long = 2 * 60 * 60 * 1000 + 30 * 60 * 1000

/**
 * 3 часа в миллисекундах
 */
const val THREE_HOURS: Long = 3 * 60 * 60 * 1000

/**
 * 3 с половиной часа в миллисекундах
 */
const val THREE_AND_HALF_HOURS: Long = 3 * 60 * 60 * 1000 + 30 * 60 * 1000

/**
 * 4 часа в миллисекундах
 */
const val FOUR_HOURS: Long = 4 * 60 * 60 * 1000

/**
 * Фрагмент брони(выбор даты и времени)
 */
class BookingFragment : Fragment(), AdapterDays.OnDateListener, AdapterBookingDuration.OnDurationListener,
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
    private val booking: ArrayList<BookDuration> = ArrayList()

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
     * Выбранная дата
     */
    private var selectedDate: Int = 0

    /**
     * Объект базы данных для получения графика
     */
    private lateinit var dataBase: VisitingHoursDB

    /**
     * График работы ресторана
     */
    private lateinit var timeTable: ArrayList<visitingHoursDTO>

    /**
     * Индикатор того, что ресторан закрывается ночью следующего дня
     */
    private var overlap: Boolean = false

    /**
     * Доступна ли бронь на сегодня
     */
    private var isTodayAvailable: Boolean = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        bookingView = inflater.inflate(R.layout.fragment_booking, container, false)

        dataBase = VisitingHoursDB(context!!) // Инициализация БД
        timeTable = dataBase.getVisitingHours() // Получение графика работы ресторана
        dataBase.clearTableDB()

        initCalendar()
        updateSchedule()
        initBookingDurationList()
        initCalendarRecycler()
        initDurationRecycler()
        selectedDate = 0

        timePickerDialog.setOnPositiveClickListener(this) // Установка обработчика для позитивной кнопки таймпикера
        bookingView.book_button.setOnClickListener(onClickListener) // Установка обработчика для кнопки выбрать столик
        bookingView.book_time_select.setOnClickListener(onClickListener) // Установка обработчика для поля время

        return bookingView
    }

    /**
     * Обновляет переменные timeOpen и timeClose в соответствии с выбранной датой
     * Обновляет параметр overlap
     */
    private fun updateSchedule() {
        val dayOfWeekFormat = SimpleDateFormat("E") // Шаблон для вывода дня недели
        val dayOfWeek = dayOfWeekFormat.format(calendar.time)
        for (i in 0 until timeTable.size) {
            if (timeTable[i].week_day.toLowerCase() == dayOfWeek.toLowerCase()) {
                Log.d("TOPKEK", "Выбранный день недели $dayOfWeek")
                timeOpen = timeTable[i].time_from
                timeClose = timeTable[i].time_to

                var scanner = Scanner(timeOpen)
                var timeScanner = Scanner(scanner.next())
                timeScanner.useDelimiter(":")
                val hoursOpen = timeScanner.nextInt()

                scanner = Scanner(timeClose)
                timeScanner = Scanner(scanner.next())
                timeScanner.useDelimiter(":")
                val hoursClose = timeScanner.nextInt()

                overlap = hoursClose < hoursOpen

                if (isTodayAvailable && selectedDate == 0) {
                    val localCalendar = Calendar.getInstance()
                    val currentHour = localCalendar.get(Calendar.HOUR_OF_DAY) + 4
                    if (currentHour > hoursOpen) {
                        Log.d("TOPKEK", "Необходимо ограничить время начала брони")
                        timeOpen = "$currentHour:00:00"
                    }
                }
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
        checkAvailableTime()
    }

    /**
     * Проверяет доступное время брони
     * TODO допилить
     */
    private fun checkAvailableTime() {
        val localCalendar = Calendar.getInstance()
        localCalendar.time = calendar.time

        // При необходимости на 1 увеличим день окончания работы ресторана
        if (overlap) localCalendar.add(Calendar.DATE, 1)

        val format = SimpleDateFormat("HH:mm:ss")
        val hourOfClose = format.parse(timeClose).hours
        val minutesOfClose = format.parse(timeClose).minutes
        localCalendar.set(Calendar.HOUR_OF_DAY, hourOfClose)
        localCalendar.set(Calendar.MINUTE, minutesOfClose)

        Log.d("TOPKEK", "Закрытие в ${localCalendar.time}")
        Log.d("TOPKEK", "Выбранное время $bookTimeAndDate")
        val timeClose: Long = localCalendar.timeInMillis
        val timeSelected = bookTimeAndDate!!.time
        val difference: Long = timeClose - timeSelected
        //Log.d("TOPKEK", "Разница в миллисекундах $difference")

        when { // Посмотрим разницу между выбранным временем и временем закрытия ресторана
            difference >= FOUR_HOURS -> { // Осталось больше 4 часов
                Log.d("TOPKEK", "Осталось 4+ часа")
                updateDurationVisibility(5)
            }
            difference == THREE_AND_HALF_HOURS -> { // Осталось 3.5 часа
                Log.d("TOPKEK", "3 с половиной часа доступно")
                updateDurationVisibility(4)
            }
            difference == THREE_HOURS -> { // Осталось 3 часа
                Log.d("TOPKEK", "3 часа доступно")
                updateDurationVisibility(3)
            }
            difference == TWO_AND_HALF_HOURS -> { // Осталось 2.5 часа
                Log.d("TOPKEK", "2 с половиной часа доступно")
                updateDurationVisibility(2)
            }
            difference == TWO_HOURS -> { // Осталось 2 часа
                Log.d("TOPKEK", "2 часа доступно")
                updateDurationVisibility(1)


            }
            else -> { // Осталось меньше двух часов (забронировать вообще нельзя)
                Log.d("TOPKEK", "Меньше двух часов")
                Toast.makeText(
                    context,
                    "От начала вашей брони до закрытия ресторана менее двух часов",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    }

    /**
     * Обновляет видимость доступных вариантов выбора длительности брони
     * @param count Количество доступных вариантов брони
     */
    private fun updateDurationVisibility(count: Int) {
        selectedDuration = 0
        bookingView.book_duration_recycler.findViewHolderForAdapterPosition(0)!!.itemView.performClick() // Сбросим выбор на 2 часа
        val globalCount = booking.size // Всего вариантов длительности брони
        for (i in 0 until count) { // Сделаем видимыми доступные варианты
            booking[i].isVisible = true
        }
        for (i in count until globalCount) { // Скроем остальные
            booking[i].isVisible = false
        }
        bookingView.book_duration_recycler.adapter!!.notifyItemRangeChanged(
            0,
            booking.size
        ) // Уведомим адаптер об изменении элементоы
    }

    /**
     * Открывает список доступных столов
     */
    private fun openTableList() {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd") // Форматирование для даты
        val timeFormat = SimpleDateFormat("HH:mm:ss") // Форматирование для времени

        val intent = Intent(activity, TableSelectActivity::class.java)
        intent.putExtra("book_date_begin", dateFormat.format(bookTimeAndDate!!.time)) // Заполняем дату брони
        intent.putExtra("book_time_begin", timeFormat.format(calendar.timeInMillis))// Заполняем время начала брони
        intent.putExtra("book_date_end", dateFormat.format(calendar.timeInMillis)) // Заполняем дату брони
        when (selectedDuration) { // И время конца
            // 2 часа
            0 -> {
                if (timeFormat.format(calendar.timeInMillis) > timeFormat.format(calendar.timeInMillis + TWO_HOURS)) {
                    intent.putExtra("book_date_end", dateFormat.format(calendar.timeInMillis + TWO_HOURS))
                }
                intent.putExtra( // Заполняем время конца брони
                    "book_time_end",
                    timeFormat.format(calendar.timeInMillis + TWO_HOURS)
                )
            }
            // 2 часа 30 минут
            1 -> {
                if (timeFormat.format(calendar.timeInMillis) > timeFormat.format(calendar.timeInMillis + TWO_AND_HALF_HOURS)) {
                    intent.putExtra(
                        "book_date_end",
                        dateFormat.format(calendar.timeInMillis + TWO_AND_HALF_HOURS)
                    )
                }
                intent.putExtra( // Заполняем время конца брони
                    "book_time_end",
                    timeFormat.format(calendar.timeInMillis + TWO_AND_HALF_HOURS)
                )
            }
            // 3 часа
            2 -> {
                if (timeFormat.format(calendar.timeInMillis) > timeFormat.format(calendar.timeInMillis + THREE_HOURS)) {
                    intent.putExtra("book_date_end", dateFormat.format(calendar.timeInMillis + THREE_HOURS))
                }
                intent.putExtra( // Заполняем время конца брони
                    "book_time_end",
                    timeFormat.format(calendar.timeInMillis + THREE_HOURS)
                )
            }
            // 3 часа 30 минут
            3 -> {
                if (timeFormat.format(calendar.timeInMillis) > timeFormat.format(calendar.timeInMillis + THREE_AND_HALF_HOURS)) {
                    intent.putExtra(
                        "book_date_end",
                        dateFormat.format(calendar.timeInMillis + THREE_AND_HALF_HOURS)
                    )
                }
                intent.putExtra( // Заполняем время конца брони
                    "book_time_end",
                    timeFormat.format(calendar.timeInMillis + THREE_AND_HALF_HOURS)
                )
            }
            // 4 часа
            4 -> {
                if (timeFormat.format(calendar.timeInMillis) > timeFormat.format(calendar.timeInMillis + FOUR_HOURS)) {
                    intent.putExtra("book_date_end", dateFormat.format(calendar.timeInMillis + FOUR_HOURS))
                }
                intent.putExtra( // Заполняем время конца брони
                    "book_time_end",
                    timeFormat.format(calendar.timeInMillis + FOUR_HOURS)
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

        checkToday()
        if (isTodayAvailable) {
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
    private fun checkToday() {
        updateSchedule()
        val calendar: Calendar = Calendar.getInstance()
        val timeNow = calendar.timeInMillis
        Log.d("TOPKEK", "Текущее время ${calendar.time}")

        val hourOfClose: Int = SimpleDateFormat("HH:mm:ss").parse(timeClose).hours

        if (hourOfClose < calendar.get(Calendar.HOUR_OF_DAY)) {
            // Если время закрытия меньше времени открытия, значит ресторан закрывается ночью следующего дня
            calendar.add(Calendar.DATE, 1)
        }
        val minutesOfClose: Int = SimpleDateFormat("HH:mm:ss").parse(timeClose).minutes
        calendar.set(Calendar.HOUR_OF_DAY, hourOfClose)
        calendar.set(Calendar.MINUTE, minutesOfClose)
        Log.d("TOPKEK", "Закрытие ресторана в ${calendar.time}")

        val timeClose = calendar.timeInMillis
        isTodayAvailable = timeClose - timeNow > 1000 * 60 * 60 * (2 + 3)
        Log.d("TOPKEK", isTodayAvailable.toString())
    }

    /**
     * Набивает данными список с вариантами продоллжительности брони
     */
    private fun initBookingDurationList() {
        booking.clear()
        booking.add(BookDuration("2 ч", true))
        booking.add(BookDuration("2 ч\n30мин", true))
        booking.add(BookDuration("3 ч", true))
        booking.add(BookDuration("3 ч\n30мин", true))
        booking.add(BookDuration("4 ч", true))
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
            AdapterBookingDuration(booking, this)
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
        if (selectedDate != position) {
            selectedDate = position // Обновим выбранную дату
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
    }

    /**
     * Реализация интерфейса для обработки нажатий на продолжительность брони в ресайклере вне адаптера
     * @param position Позиция элемента
     */
    override fun onDurationClick(position: Int) {
        selectedDuration = position
    }
}