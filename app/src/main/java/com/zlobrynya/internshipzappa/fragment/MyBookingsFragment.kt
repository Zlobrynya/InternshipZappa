package com.zlobrynya.internshipzappa.fragment


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import com.zlobrynya.internshipzappa.R
import com.zlobrynya.internshipzappa.adapter.booking.AdapterUserBookings
import com.zlobrynya.internshipzappa.tools.retrofit.DTOs.bookingDTOs.UserBookingDTO
import kotlinx.android.synthetic.main.fragment_my_bookings.view.*

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
}
