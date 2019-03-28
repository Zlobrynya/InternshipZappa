package com.zlobrynya.internshipzappa.fragment


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import com.zlobrynya.internshipzappa.R
import com.zlobrynya.internshipzappa.activity.profile.LoginActivity
import com.zlobrynya.internshipzappa.adapter.booking.AdapterUserBookings
import com.zlobrynya.internshipzappa.tools.retrofit.DTOs.accountDTOs.verifyEmailDTO
import com.zlobrynya.internshipzappa.tools.retrofit.DTOs.bookingDTOs.UserBookingDTO
import com.zlobrynya.internshipzappa.tools.retrofit.DTOs.bookingDTOs.UserBookingList
import com.zlobrynya.internshipzappa.tools.retrofit.DTOs.bookingDTOs.deleteBookingDTO
import com.zlobrynya.internshipzappa.tools.retrofit.DTOs.respDTO
import com.zlobrynya.internshipzappa.tools.retrofit.RetrofitClientInstance
import com.zlobrynya.internshipzappa.util.StaticMethods
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_my_bookings.view.*
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

/**
 * Фрагмент мои брони
 */
class MyBookingsFragment : Fragment(), AdapterUserBookings.OnDiscardClickListener {

    /**
     * Список столиков
     */
    private val bookingList: ArrayList<UserBookingDTO> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_my_bookings, container, false)
        view.login_button.setOnClickListener(onClickListener)
        return view
    }

    override fun onResume() {
        super.onResume()
        bookingList.clear()
        val view = this.view
        if (view != null) {
            postUserBookings(view)
        }
        Log.d("BOOP", "onResume MyBookingsFragment")
    }

    /**
     * Обработчик нажатий
     */
    private val onClickListener = View.OnClickListener {
        when (it.id) {
            // Кнопка залогиниться
            R.id.login_button -> openLoginActivity()
        }
    }

    /**
     * Открывает логин активити
     */
    private fun openLoginActivity() {
        val intent = Intent(context, LoginActivity::class.java)
        startActivity(intent)
    }

    /**
     * Набивает данными список броней пользователя
     * TODO переделать под сервер, добавить проверку на пустое количество броней
     */
    private fun initBookingList(userBookingList: List<UserBookingDTO>, view: View) {
        if (userBookingList.isEmpty()) { // Если список пустой (броней нету)
            view.no_user_bookings_text_view.visibility = View.VISIBLE
            view.user_bookings_recycler.visibility = View.GONE
        } else { // Брони есть
            view.no_user_bookings_text_view.visibility = View.GONE
            view.user_bookings_recycler.visibility = View.VISIBLE
            for (i in 0 until userBookingList.size) {
                bookingList.add(userBookingList[i])
            }
            initRecyclerView(view)
        }
    }

    /**
     * Настраивает ресайклер вью
     */
    private fun initRecyclerView(view: View): View {
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        view.user_bookings_recycler.layoutManager = layoutManager
        view.user_bookings_recycler.addItemDecoration(
            DividerItemDecoration(context, DividerItemDecoration.VERTICAL) // Разделитель элементов внутри ресайклера
        )
        view.user_bookings_recycler.adapter = AdapterUserBookings(bookingList, this)
        return view
    }

    /**
     * Реализация интерфейса для обработки нажатий вне адаптера
     * @param position Позиция элемента
     * @param isButtonClick Произошло ли нажатие на кнопку "Отменить"
     */
    override fun onDiscardClick(position: Int, isButtonClick: Boolean) {
        if (isButtonClick) {
            prepare(position)
        }
    }

    /**
     * Проверяет доступ в интернет и в зависимости от результата вызывает showNoInternetConnectionAlert или preparePostParams
     */
    private fun prepare(position: Int) {
        if (!StaticMethods.checkInternetConnection(context)) showNoInternetConnectionAlert(position)
        else confirmDiscard(position)
    }

    /**
     * Получает столики для текущего авторизированного пользователя
     */
    private fun postUserBookings(view: View) {

        val sharedPreferences =
            activity!!.getSharedPreferences(this.getString(R.string.user_info), Context.MODE_PRIVATE)
        val newEmail = verifyEmailDTO()
        newEmail.email = sharedPreferences.getString(this.getString(R.string.user_email), "")
        val jwt = sharedPreferences.getString(this.getString(R.string.access_token), "null").toString()

        RetrofitClientInstance.getInstance()
            .postUserBookings(jwt, newEmail)
            .subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe(object : Observer<Response<UserBookingList>> {

                override fun onComplete() {}

                override fun onSubscribe(d: Disposable) {}

                override fun onNext(t: Response<UserBookingList>) {
                    Log.i("checkMyBooking", "${t.code()}")

                    if (t.isSuccessful) {
                        /**
                         * Если юзер авторизован, то вызываем initBookingList для наполнения списка броней
                         */
                        val userBookingList = t.body()
                        if (userBookingList != null) {
                            view.user_not_authorized.visibility = View.GONE
                            view.login_button.visibility = View.GONE
                            initBookingList(userBookingList.bookings, view)
                        }
                    } else {
                        /*
                        Если юзер не авторизован, то скрываем рейсайклер и выводим сообщение
                         */
                        if (t.code() == 401) {
                            view.user_not_authorized.visibility = View.VISIBLE
                            view.no_user_bookings_text_view.visibility = View.GONE
                            view.user_bookings_recycler.visibility = View.GONE
                            view.login_button.visibility = View.VISIBLE
                        }
                    }
                }

                override fun onError(e: Throwable) {
                    Log.i("check", "that's not fineIn")
                    //запрос не выполнен, всё плохо
                }

            })
    }

    /**
     * Удаляет бронь по id из списка
     * @param bookingId id элемента
     */
    private fun postDeleteUserBookings(bookingId: Int) {

        val sharedPreferences =
            activity!!.getSharedPreferences(this.getString(R.string.user_info), Context.MODE_PRIVATE)
        val newDeleteBooking = deleteBookingDTO()
        newDeleteBooking.email = sharedPreferences.getString(this.getString(R.string.user_email), "")!!.toString()
        newDeleteBooking.booking_id = bookingId
        val jwt = sharedPreferences.getString(this.getString(R.string.access_token), "null")!!.toString()

        RetrofitClientInstance.getInstance()
            .postDeleteUserBookings(jwt, newDeleteBooking)
            .subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe(object : Observer<Response<respDTO>> {

                override fun onComplete() {}

                override fun onSubscribe(d: Disposable) {}

                override fun onNext(t: Response<respDTO>) {
                    Log.i("checkMyBooking", "${t.code()}")

                    if (t.isSuccessful) {
                        /*
                        Юзер авторизован, удаляем бронь
                         */
                        this@MyBookingsFragment.onResume()
                    } else {
                        /*
                        Юзер не авторизован, пишем что сессия истекла
                         */
                        if (t.code() == 401) {
                            Toast.makeText(context, "Ваша сессия истекла, авторизуйтесь повторно", Toast.LENGTH_SHORT)
                                .show()
                            val view = this@MyBookingsFragment.view
                            if (view != null) {
                                view.user_not_authorized.visibility = View.VISIBLE
                                view.no_user_bookings_text_view.visibility = View.GONE
                                view.user_bookings_recycler.visibility = View.GONE
                            }
                        }
                    }
                }

                override fun onError(e: Throwable) {
                    Log.i("check", "that's not fineIn")
                    //запрос не выполнен, всё плохо
                }

            })
    }

    /**
     * Выводит с просьбой отменить бронь
     */
    private fun confirmDiscard(position: Int) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context as Context, R.style.AlertDialogCustom)
        val booking: UserBookingDTO = bookingList[position]
        builder.setTitle("Отменить бронирование")
            .setMessage(
                "Вы уверены, что хотите отменить бронь на " +
                        "${getDate(booking.date_time_from)} " +
                        "${getTime(booking.date_time_from)} - ${getTime(booking.date_time_to)}?"
            )
            .setCancelable(false)
            .setPositiveButton("Да") { dialog, which ->
                run {
                    dialog.dismiss()
                    postDeleteUserBookings(bookingList[position].booking_id)
                }
            }
            .setNegativeButton("Нет") { dialog, which -> dialog.dismiss() }
        val alert = builder.create()
        alert.show()
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(resources.getColor(R.color.color_accent))
        alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(resources.getColor(R.color.color_accent))
    }

    /**
     * Возвращает число в строковом представлении
     * @param date Дата в формате 2019-03-09 18:30:00
     * @return Число в строковом представлении с указанием месяца (пр. 28 сентября)
     */
    private fun getDate(date: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val parsedDate = inputFormat.parse(date)
        val outputFormat = SimpleDateFormat("d.MM.yyyy", Locale.getDefault())
        return outputFormat.format(parsedDate)
    }

    /**
     * Возвращает время в строковом представлении в формате 18:30
     * @param date Дата в формате 2019-03-09 18:30:00
     * @return Время в строковом представлении в формате 18:30
     */
    private fun getTime(date: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val parsedDate = inputFormat.parse(date)
        val outputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        return outputFormat.format(parsedDate)
    }

    /**
     * Выводит диалоговое окно с сообщением об отсутствии интернета
     */
    private fun showNoInternetConnectionAlert(position: Int) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context as Context, R.style.AlertDialogCustom)
        builder.setTitle("Ошибка соединения")
            .setMessage("Без подключения к сети невозможно продолжить бронирование.\nПроверьте соединение и попробуйте снова")
            .setCancelable(false)
            .setPositiveButton("ПОВТОРИТЬ") { dialog, which ->
                run {
                    dialog.dismiss()
                    prepare(position)
                }
            }
            .setNegativeButton("Отмена") { dialog, which -> dialog.dismiss() }
        val alert = builder.create()
        alert.show()
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(resources.getColor(R.color.color_accent))
        alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(resources.getColor(R.color.color_accent))
    }
}
