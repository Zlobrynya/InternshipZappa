package com.zlobrynya.narogah.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.SystemClock
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.zlobrynya.narogah.R
import com.zlobrynya.narogah.adapter.booking.AdapterBookingDuration
import com.zlobrynya.narogah.adapter.booking.AdapterDays
import com.zlobrynya.narogah.adapter.booking.BookDuration
import com.zlobrynya.narogah.tools.database.VisitingHoursDB
import com.zlobrynya.narogah.tools.retrofit.DTOs.bookingDTOs.bookingDataDTO
import com.zlobrynya.narogah.tools.retrofit.DTOs.bookingDTOs.tableList
import com.zlobrynya.narogah.tools.retrofit.DTOs.bookingDTOs.visitingHoursDTO
import com.zlobrynya.narogah.tools.retrofit.RetrofitClientInstance
import com.zlobrynya.narogah.util.CustomTimePickerDialog
import com.zlobrynya.narogah.util.PositiveClickListener
import com.zlobrynya.narogah.util.StaticMethods
import com.zlobrynya.narogah.util.TableParceling
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_booking.*
import kotlinx.android.synthetic.main.fragment_booking.view.*
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

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
    private lateinit var timePickerDialog: CustomTimePickerDialog

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

        /*dataBase = VisitingHoursDB(context!!) // Инициализация БД
        timeTable = dataBase.getVisitingHours() // Получение графика работы ресторана

        for (i in 0 until timeTable.size) {
            Log.d("LMAO", "${timeTable[i].time_from} , ${timeTable[i].time_to}, ${timeTable[i].week_day} ")
        }
        dataBase.closeDataBase()*/

        timeTable = ArrayList()
        timeTable.add(visitingHoursDTO("12:00:00", "23:00:00", "пн"))
        timeTable.add(visitingHoursDTO("12:00:00", "23:00:00", "вт"))
        timeTable.add(visitingHoursDTO("12:00:00", "23:00:00", "ср"))
        timeTable.add(visitingHoursDTO("12:00:00", "23:00:00", "чт"))
        timeTable.add(visitingHoursDTO("12:00:00", "01:00:00", "пт"))
        timeTable.add(visitingHoursDTO("13:00:00", "01:00:00", "сб"))
        timeTable.add(visitingHoursDTO("13:00:00", "22:00:00", "вс"))

        initCalendar()
        updateSchedule()
        initBookingDurationList()
        initCalendarRecycler()
        initDurationRecycler()

        selectedDate = 0

        //timePickerDialog.setOnPositiveClickListener(this) // Установка обработчика для позитивной кнопки таймпикера
        bookingView.book_button.setOnClickListener(onClickListener) // Установка обработчика для кнопки выбрать столик
        bookingView.book_time_select.setOnClickListener(onClickListener) // Установка обработчика для поля время

        return bookingView
    }

    override fun onResume() {
        super.onResume()
        updateGui()
    }

    /**
     * В текстовом поле меняем время брони на ближайшее и убираем недоступные варианты брони
     */
    private fun updateGui(click: Boolean = false) {
        bookingView.book_time_select.text = timeOpen.substring(0, timeOpen.length - 3)

        val localCalendar = Calendar.getInstance()
        val format = SimpleDateFormat("HH:mm:ss")

        localCalendar.set(Calendar.HOUR_OF_DAY, format.parse(timeOpen).hours)
        localCalendar.set(Calendar.MINUTE, format.parse(timeOpen).minutes)
        localCalendar.set(Calendar.SECOND, 0)
        val begin = localCalendar.timeInMillis
        calendar.set(Calendar.HOUR_OF_DAY, format.parse(timeOpen).hours)
        calendar.set(Calendar.MINUTE, format.parse(timeOpen).minutes)
        calendar.set(Calendar.SECOND, 0)
        bookTimeAndDate = calendar.time
        //setInitialDateTime()
        if (overlap) localCalendar.add(Calendar.DATE, 1)

        localCalendar.set(Calendar.HOUR_OF_DAY, format.parse(timeClose).hours)
        localCalendar.set(Calendar.MINUTE, format.parse(timeClose).minutes)
        val end = localCalendar.timeInMillis


        val difference: Long = end - begin

        when { // Посмотрим разницу между выбранным временем и временем закрытия ресторана
            difference >= FOUR_HOURS -> { // Осталось больше 4 часов
                updateDurationVisibility(5, click)
            }
            difference == THREE_AND_HALF_HOURS -> { // Осталось 3.5 часа
                updateDurationVisibility(4, click)
            }
            difference == THREE_HOURS -> { // Осталось 3 часа
                updateDurationVisibility(3, click)
            }
            difference == TWO_AND_HALF_HOURS -> { // Осталось 2.5 часа
                updateDurationVisibility(2, click)
            }
            difference == TWO_HOURS -> { // Осталось 2 часа
                updateDurationVisibility(1, click)


            }
            else -> { // Осталось меньше двух часов (забронировать вообще нельзя)
                Toast.makeText(
                    context,
                    "От начала вашей брони до закрытия ресторана менее двух часов",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
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
                        timeOpen = "$currentHour:00:00"
                    }
                }
                break
            }
        }

        //bookTimeAndDate = null // Сбросим выбранное время
    }

    var lastCLickTime: Long = 0

    /**
     * Обработчик нажатий
     */
    private val onClickListener = View.OnClickListener {
        when (it.id) {
            // Кнопка выбрать стол
            R.id.book_button -> {
                if (SystemClock.elapsedRealtime() - lastCLickTime < 1000) {
                    return@OnClickListener
                } else {
                    lastCLickTime = SystemClock.elapsedRealtime()
                    if (bookTimeAndDate != null) { // Проверим, выбрал ли пользователь время
                        bookingView.book_time_select_label.error = null // Скроем варнинг

                        //openTableList()
                        prepare()

                    } else bookingView.book_time_select_label.error = "Выберите время" // Выведем варнинг
                }
            }
            // Поле "Выберите время"
            R.id.book_time_select -> {
                if (SystemClock.elapsedRealtime() - lastCLickTime < 1000) {
                    return@OnClickListener
                } else {
                    lastCLickTime = SystemClock.elapsedRealtime()
                    showTimePickerDialog()
                }
            }
        }
    }


    /**
     * Открывает таймпикер
     */
    private fun showTimePickerDialog() {
        timePickerDialog = CustomTimePickerDialog()
        timePickerDialog.setOnPositiveClickListener(this)
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
        calendar.set(Calendar.SECOND, 0)
        setInitialDateTime()
        checkAvailableTime()
    }

    /**
     * Проверяет доступное время брони
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
        localCalendar.set(Calendar.SECOND, 0)

        val timeClose: Long = localCalendar.timeInMillis
        val timeSelected = bookTimeAndDate!!.time
        val difference: Long = timeClose - timeSelected
        //Log.d("TOPKEK", "Разница в миллисекундах $difference")

        when { // Посмотрим разницу между выбранным временем и временем закрытия ресторана
            difference >= FOUR_HOURS -> { // Осталось больше 4 часов
                updateDurationVisibility(5, true)
            }
            difference == THREE_AND_HALF_HOURS -> { // Осталось 3.5 часа
                updateDurationVisibility(4, true)
            }
            difference == THREE_HOURS -> { // Осталось 3 часа
                updateDurationVisibility(3, true)
            }
            difference == TWO_AND_HALF_HOURS -> { // Осталось 2.5 часа
                updateDurationVisibility(2, true)
            }
            difference == TWO_HOURS -> { // Осталось 2 часа
                updateDurationVisibility(1, true)


            }
            else -> { // Осталось меньше двух часов (забронировать вообще нельзя)
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
    private fun updateDurationVisibility(count: Int, click: Boolean) {
        if (click) selectedDuration = 0

        // Сбросим выбор на 2 часа
        if (click) bookingView.book_duration_recycler.findViewHolderForAdapterPosition(0)!!.itemView.performClick()
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
     * Открывает список доступных столов (фрагмент)
     */
    private fun preparePostParams() {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd") // Форматирование для даты
        val timeFormat = SimpleDateFormat("HH:mm:ss") // Форматирование для времени

        val args = Bundle()
        args.putString("book_date_begin", dateFormat.format(bookTimeAndDate!!.time)) // Заполняем дату начала брони
        args.putString("book_time_begin", timeFormat.format(calendar.timeInMillis)) // Заполняем время начала брони
        args.putString("book_date_end", dateFormat.format(calendar.timeInMillis)) // Заполняем дату конца брони
        val newBooking = bookingDataDTO() // Объект для POST запроса
        newBooking.date = dateFormat.format(bookTimeAndDate!!.time)
        newBooking.time_from = timeFormat.format(calendar.timeInMillis)

        when (selectedDuration) { // И время конца
            // 2 часа
            0 -> {
                if (timeFormat.format(calendar.timeInMillis) > timeFormat.format(calendar.timeInMillis + TWO_HOURS)) {
                    args.putString("book_date_end", dateFormat.format(calendar.timeInMillis + TWO_HOURS))
                    newBooking.date_to = dateFormat.format(calendar.timeInMillis + TWO_HOURS)
                }
                args.putString("book_time_end", timeFormat.format(calendar.timeInMillis + TWO_HOURS))
                newBooking.date_to = dateFormat.format(calendar.timeInMillis + TWO_HOURS)
                newBooking.time_to = timeFormat.format(calendar.timeInMillis + TWO_HOURS)
            }
            // 2 часа 30 минут
            1 -> {
                if (timeFormat.format(calendar.timeInMillis) > timeFormat.format(calendar.timeInMillis + TWO_AND_HALF_HOURS)) {
                    args.putString("book_date_end", dateFormat.format(calendar.timeInMillis + TWO_AND_HALF_HOURS))
                    newBooking.date_to = dateFormat.format(calendar.timeInMillis + TWO_AND_HALF_HOURS)
                }
                args.putString("book_time_end", timeFormat.format(calendar.timeInMillis + TWO_AND_HALF_HOURS))
                newBooking.date_to = dateFormat.format(calendar.timeInMillis + TWO_AND_HALF_HOURS)
                newBooking.time_to = timeFormat.format(calendar.timeInMillis + TWO_AND_HALF_HOURS)
            }
            // 3 часа
            2 -> {
                if (timeFormat.format(calendar.timeInMillis) > timeFormat.format(calendar.timeInMillis + THREE_HOURS)) {
                    args.putString("book_date_end", dateFormat.format(calendar.timeInMillis + THREE_HOURS))
                    newBooking.date_to = dateFormat.format(calendar.timeInMillis + THREE_HOURS)
                }
                args.putString("book_time_end", timeFormat.format(calendar.timeInMillis + THREE_HOURS))
                newBooking.date_to = dateFormat.format(calendar.timeInMillis + THREE_HOURS)
                newBooking.time_to = timeFormat.format(calendar.timeInMillis + THREE_HOURS)
            }
            // 3 часа 30 минут
            3 -> {
                if (timeFormat.format(calendar.timeInMillis) > timeFormat.format(calendar.timeInMillis + THREE_AND_HALF_HOURS)) {
                    args.putString("book_date_end", dateFormat.format(calendar.timeInMillis + THREE_AND_HALF_HOURS))
                    newBooking.date_to = dateFormat.format(calendar.timeInMillis + THREE_AND_HALF_HOURS)
                }
                args.putString("book_time_end", timeFormat.format(calendar.timeInMillis + THREE_AND_HALF_HOURS))
                newBooking.date_to = dateFormat.format(calendar.timeInMillis + THREE_AND_HALF_HOURS)
                newBooking.time_to = timeFormat.format(calendar.timeInMillis + THREE_AND_HALF_HOURS)
            }
            // 4 часа
            4 -> {
                if (timeFormat.format(calendar.timeInMillis) > timeFormat.format(calendar.timeInMillis + FOUR_HOURS)) {
                    args.putString("book_date_end", dateFormat.format(calendar.timeInMillis + FOUR_HOURS))
                    newBooking.date_to = dateFormat.format(calendar.timeInMillis + FOUR_HOURS)
                }
                args.putString("book_time_end", timeFormat.format(calendar.timeInMillis + FOUR_HOURS))
                newBooking.date_to = dateFormat.format(calendar.timeInMillis + FOUR_HOURS)
                newBooking.time_to = timeFormat.format(calendar.timeInMillis + FOUR_HOURS)
            }
        }
        networkRxJavaPost(newBooking, args)
    }

    /**
     * Набивает данными список с датами
     */
    private fun initCalendar() {
        this.calendar = Calendar.getInstance() // Обновим глобальную переменную с календарем
        schedule.clear()
        val localCalendar: Calendar = Calendar.getInstance() // Локальная переменная с календарем для заполнения списков
        localCalendar.set(Calendar.SECOND, 0)

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
    @SuppressLint("SimpleDateFormat")
    private fun checkToday() {
        updateSchedule()
        val calendar: Calendar = Calendar.getInstance()
        calendar.set(Calendar.SECOND, 0)
        val timeNow = calendar.timeInMillis


        val hourOfClose: Int = SimpleDateFormat("HH:mm:ss").parse(timeClose).hours

        if (hourOfClose < calendar.get(Calendar.HOUR_OF_DAY)) {
            // Если время закрытия меньше времени открытия, значит ресторан закрывается ночью следующего дня
            calendar.add(Calendar.DATE, 1)
        }
        val minutesOfClose: Int = SimpleDateFormat("HH:mm:ss").parse(timeClose).minutes
        calendar.set(Calendar.HOUR_OF_DAY, hourOfClose)
        calendar.set(Calendar.MINUTE, minutesOfClose)
        calendar.set(Calendar.SECOND, 0)

        val timeClose = calendar.timeInMillis
        isTodayAvailable = timeClose - timeNow > 1000 * 60 * 60 * (2 + 3)
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
        bookingView.days_recycler.adapter = AdapterDays(schedule, this, context)
    }

    /**
     * Настраивает ресайклер для вариантов длительности бронирования
     */
    private fun initDurationRecycler() {
        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        bookingView.book_duration_recycler.layoutManager = layoutManager
        bookingView.book_duration_recycler.adapter =
            AdapterBookingDuration(booking, this, context)
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
        calendar.set(Calendar.SECOND, 0)
        bookTimeAndDate = calendar.time // Запишем выбранное время
    }

    /**
     * Реализация интерфейса для обработки нажатий на числа в ресайклере вне адаптера
     * @param position Позиция элемента
     */
    override fun onDateClick(position: Int) {
        if (selectedDate != position) {
            selectedDate = position // Обновим выбранную дату
            calendar.set(Calendar.DATE, schedule[position].date) // Установим в календарь выбранную дату
            calendar.set(Calendar.MONTH, schedule[position].month)
            calendar.set(Calendar.YEAR, schedule[position].year + 1900)
            calendar.set(Calendar.SECOND, 0)
            updateSchedule() // Обновим расписание
            //book_time_select.text = "Время" // Обновим значение во вьюшке
            updateGui(true)
        }
    }

    /**
     * Реализация интерфейса для обработки нажатий на продолжительность брони в ресайклере вне адаптера
     * @param position Позиция элемента
     */
    override fun onDurationClick(position: Int) {
        selectedDuration = position
    }

    /**
     * Выводит диалоговое окно с сообщением об отсутствии интернета
     */
    private fun showNoInternetConnectionAlert() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context as Context, R.style.AlertDialogCustom)
        builder.setTitle("Ошибка соединения")
            .setMessage("Без подключения к сети невозможно продолжить бронирование.\nПроверьте соединение и попробуйте снова")
            .setCancelable(false)
            .setPositiveButton("ПОВТОРИТЬ") { dialog, which ->
                run {
                    dialog.dismiss()
                    prepare()
                }
            }
            .setNegativeButton("ОТМЕНА") { dialog, which -> dialog.dismiss() }
        val alert = builder.create()
        alert.show()
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(resources.getColor(R.color.color_accent))
        alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(resources.getColor(R.color.color_accent))
    }

    /**
     * Проверяет доступ в интернет и в зависимости от результата вызывает showNoInternetConnectionAlert или preparePostParams
     */
    private fun prepare() {
        if (!StaticMethods.checkInternetConnection(context)) showNoInternetConnectionAlert()
        else preparePostParams()
    }

    /**
     * Отправляет POST запрос на сервер и получает в ответе список доступных столиков(RxJava2)
     */
    private fun networkRxJavaPost(newBooking: bookingDataDTO, args: Bundle) {

        val view = this.view
        if (view != null) {
            view.progress_spinner.visibility = View.VISIBLE
        }

        RetrofitClientInstance.getInstance()
            .postBookingDate(newBooking)
            .subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe(object : Observer<Response<tableList>> {

                override fun onComplete() {}

                override fun onSubscribe(d: Disposable) {}

                override fun onNext(t: Response<tableList>) {
                    val view = this@BookingFragment.view
                    if (view != null) {
                        if (t.isSuccessful) {
                            if (t.body() != null) {
                                if (t.body()!!.data.isEmpty()) { // Если свободных столиков нету
                                    showNoTablesAvailableAlert()
                                } else { // Если свободные столики есть
                                    val responseBody = t.body() as tableList
                                    openTableSelectFragment(args, responseBody)
                                }
                            } else { // Если свободных столиков нету
                            }
                        } else { // В случае ошибок

                        }
                        view.progress_spinner.visibility = View.GONE
                    }
                }

                override fun onError(e: Throwable) {

                }
            })
    }

    /**
     * Открывает фрагмент со списком столов
     * @param args Аргументы
     */
    private fun openTableSelectFragment(args: Bundle, tableList: tableList) {
        val trans = fragmentManager!!.beginTransaction()
        val tableSelectFragment = TableSelectFragment()
        args.putParcelable("table_list", TableParceling(tableList))
        tableSelectFragment.arguments = args
        trans.add(R.id.root_frame, tableSelectFragment, "TABLE_SELECT")
        trans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        trans.addToBackStack("TABLE_SELECT")
        trans.commit()
    }

    /**
     * Выводит алерт об отсутствии столиков
     */
    private fun showNoTablesAvailableAlert() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context as Context, R.style.AlertDialogCustom)
        builder.setTitle("Все столы заняты")
            .setMessage("Измените время, дату или продолжительность визита и попробуйте снова")
            .setCancelable(false)
            .setPositiveButton("OK") { dialog, which -> dialog.dismiss() }
        val alert = builder.create()
        alert.show()
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(resources.getColor(R.color.color_accent))
    }

}