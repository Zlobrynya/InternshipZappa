package com.zlobrynya.internshipzappa.fragment


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import com.zlobrynya.internshipzappa.R
import com.zlobrynya.internshipzappa.activity.profile.CodeFEmailActivity
import com.zlobrynya.internshipzappa.adapter.booking.AdapterUserBookings
import com.zlobrynya.internshipzappa.tools.retrofit.DTOs.accountDTOs.verifyEmailDTO
import com.zlobrynya.internshipzappa.tools.retrofit.DTOs.accountDTOs.verifyRespDTO
import com.zlobrynya.internshipzappa.tools.retrofit.DTOs.bookingDTOs.UserBookingDTO
import com.zlobrynya.internshipzappa.tools.retrofit.DTOs.bookingDTOs.UserBookingList
import com.zlobrynya.internshipzappa.tools.retrofit.DTOs.bookingDTOs.deleteBookingDTO
import com.zlobrynya.internshipzappa.tools.retrofit.DTOs.respDTO
import com.zlobrynya.internshipzappa.tools.retrofit.RetrofitClientInstance
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.fragment_my_bookings.view.*
import retrofit2.Response

/**
 * Фрагмент мои брони
 */
class MyBookingsFragment : Fragment(), AdapterUserBookings.OnDiscardClickListener {

    /**
     * Список столиков
     */
    private val bookingList: ArrayList<UserBookingDTO> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.fragment_my_bookings, container, false)
        initBookingList()
        view = initRecyclerView(view)
        return view
    }

    /**
     * Набивает данными список броней пользователя
     * TODO переделать под сервер, добавить проверку на пустое количество броней
     */
    private fun initBookingList() {
        bookingList.add(
            UserBookingDTO(
                false,
                486,
                "2019-03-09 18:30:00",
                "2019-03-09 20:00:00",
                1,
                4,
                "Диваны",
                "У окна"
            )
        )
        bookingList.add(
            UserBookingDTO(
                true,
                487,
                "2019-03-09 18:30:00",
                "2019-03-09 20:00:00",
                3,
                6,
                "Диваны",
                "У бара"
            )
        )
        bookingList.add(
            UserBookingDTO(
                false,
                488,
                "2019-03-09 18:30:00",
                "2019-03-09 20:00:00",
                4,
                8,
                "Стулья",
                "У входа"
            )
        )
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
        // TODO Сделать реальную отмену брони
        if (isButtonClick) Toast.makeText(context, "Отмена брони", Toast.LENGTH_SHORT).show()
    }

    /**
     * получает столики для текущего авторизированного пользователя (проверил, работает)
     * TODO воткнуть и обработать
     */
    private fun postUserBookings(){

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

                    if(t.isSuccessful) {
                        /**
                         * TODO юзер авторизирован и запрос прошёл, сюда пихнуть обработку
                         */
                        //
                    }else{
                        /**
                         * TODO
                         * юзер неавторизирован или ещё какая херня, но запрос выполнен. Посмотреть код t.code() и обработать
                         *если 401 запустить активити авторизации, если успешно авторизовался выкинуть обратно сюда и обновить
                         *содержимое фрагмента, видимо через отслеживание результата активити опять, хз
                         */
                    }
                }

                override fun onError(e: Throwable) {
                    Log.i("check", "that's not fineIn")
                    //запрос не выполнен, всё плохо
                }

            })
    }

    /**
     * удаляет бронь по id из списка (не проверял, пока никак)
     * @param bookingId id элемента
     * TODO воткнуть и обработать
     */
    private fun postDeleteUserBookings(bookingId: Int){

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

                    if(t.isSuccessful) {
                        /**
                         * TODO юзер авторизирован и запрос прошёл, сюда пихнуть обработку
                         */
                        //
                    }else{
                        /**
                         * TODO
                         * юзер неавторизирован или ещё какая херня, но запрос выполнен. Посмотреть код t.code() и обработать
                         *если 401 запустить активити авторизации, если успешно авторизовался выкинуть обратно сюда и обновить
                         *содержимое фрагмента, видимо через отслеживание результата активити опять, хз
                         */
                    }
                }

                override fun onError(e: Throwable) {
                    Log.i("check", "that's not fineIn")
                    //запрос не выполнен, всё плохо
                }

            })
    }
}
