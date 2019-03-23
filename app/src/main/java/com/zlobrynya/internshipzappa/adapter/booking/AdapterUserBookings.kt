package com.zlobrynya.internshipzappa.adapter.booking

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.zlobrynya.internshipzappa.tools.retrofit.DTOs.bookingDTOs.UserBookingDTO
import kotlinx.android.synthetic.main.item_user_booking.view.*
import java.text.SimpleDateFormat
import java.util.*

class AdapterUserBookings(
    private val values: ArrayList<UserBookingDTO>, onDiscardClickListener: OnDiscardClickListener
) : RecyclerView.Adapter<AdapterUserBookings.ViewHolder>() {

    private val mOnDiscardClickListener = onDiscardClickListener

    /**
     * Размер коллекции
     */
    override fun getItemCount(): Int {
        return values.size
    }

    /**
     * Устанавливает в ViewHolder нужные данные
     * @param holder Экземпляр ViewHolder
     * @param position Позиция элемента
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val booking = values[position]

        holder.bookDateInfo.text =
            "${getDate(booking.date_time_from)}, ${getTime(booking.date_time_from)} - ${getTime(booking.date_time_to)}"

        holder.seatInfo.text =
            "№${booking.table_id}, ${booking.seat_count} ${getCase(booking.seat_count)}, ${booking.seat_place}, ${booking.seat_type}"

        if (booking.accepted) {
            holder.discardButton.visibility = View.VISIBLE
            holder.processInfo.visibility = View.GONE
            holder.processLogo.visibility = View.GONE

            holder.discardButton.setOnClickListener { holder.onDiscardClickListener.onDiscardClick(position, true) }
        } else {
            holder.discardButton.visibility = View.GONE
            holder.processInfo.visibility = View.VISIBLE
            holder.processLogo.visibility = View.VISIBLE
        }

    }

    /**
     * Создает нужный ViewHolder
     */
    override fun onCreateViewHolder(parent: ViewGroup, ViewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(com.zlobrynya.internshipzappa.R.layout.item_user_booking, parent, false)
        return ViewHolder(itemView, mOnDiscardClickListener)
    }

    /**
     * ViewHolder для информации о брони
     */
    inner class ViewHolder(itemView: View, onDiscardClickListener: OnDiscardClickListener) :
        RecyclerView.ViewHolder(itemView), View.OnClickListener {

        override fun onClick(v: View?) {
            onDiscardClickListener.onDiscardClick(adapterPosition, false)
        }

        val bookDateInfo: TextView = itemView.book_date_info
        val seatInfo: TextView = itemView.seat_info
        val discardButton: Button = itemView.discard_button
        val processLogo: ImageView = itemView.process_logo
        val processInfo: TextView = itemView.process_info
        val onDiscardClickListener: OnDiscardClickListener

        init {
            itemView.setOnClickListener(this)
            this.onDiscardClickListener = onDiscardClickListener
        }
    }

    /**
     * Возвращает число в строковом представлении
     * @param date Дата в формате 2019-03-09 18:30:00
     * @return Число в строковом представлении с указанием месяца (пр. 28 сентября)
     */
    private fun getDate(date: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val parsedDate = inputFormat.parse(date)
        val outputFormat = SimpleDateFormat("d MMMM", Locale.getDefault())
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
     * Возвращает "мест" или "места" в зависимости от числа мест
     * @param count Число мест
     * @return "мест" или "места" в зависимости от числа мест
     */
    private fun getCase(count: Int): String {
        return when (count) {
            1 -> "место"
            in 2..4 -> "места"
            else -> "мест"
        }
    }

    /**
     * Интерфейс для обработки нажатий вне адаптера
     */
    interface OnDiscardClickListener {
        /**
         * Абстрактная функция для обработки нажатий
         * @param position Позиция нажатого элемента
         * @param isButtonClick Произошло ли нажатие на кнопку "Отменить"
         */
        fun onDiscardClick(position: Int, isButtonClick: Boolean)
    }
}