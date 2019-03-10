package com.zlobrynya.internshipzappa.adapter

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import java.util.*
import android.widget.TextView
import com.zlobrynya.internshipzappa.R
import java.text.SimpleDateFormat


/**
 * Адаптер для RecyclerView, отображающий календарные дни для брони
 */
class AdapterDays(private val values: ArrayList<Date>, onNoteListener: OnNoteListener) :
    RecyclerView.Adapter<AdapterDays.ViewHolder>() {

    /**
     * Номер выбранного элемента в выборе даты
     */
    var focusedElement: Int = 0

    private val mOnNoteListener: OnNoteListener = onNoteListener

    /**
     * Принимает объект ViewHolder и устанавливает необходимые данные
     * @param holder ViewHolder
     * @param position Позиция элемента
     */
    override fun onBindViewHolder(holder: AdapterDays.ViewHolder, position: Int) {
        // По необходимости меняем цветовую обводку у квадратика с датой
        if (position == focusedElement) holder.itemView.setBackgroundResource(R.drawable.item_day_selected_shape)
        else holder.itemView.setBackgroundResource(R.drawable.item_day_shape)

        holder.dayNumber.text = getDayNumber(values[position])
        holder.dayOfWeek.text = getDayOfWeek(values[position])

    }

    /**
     * Создает новый объект ViewHolder
     * @param ViewType тип нужного ViewHolder
     */
    override fun onCreateViewHolder(parent: ViewGroup, ViewType: Int): AdapterDays.ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_day, parent, false)
        return ViewHolder(itemView, mOnNoteListener)
    }

    /**
     * Возвращает общее количество элементов списка
     */
    override fun getItemCount(): Int {
        return values.size
    }

    /**
     * Инициализация объекта ViewHolder для квадратика число-день недели
     */
    inner class ViewHolder(itemView: View, onNoteListener: OnNoteListener) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        /**
         * Реализация onClick
         * @param v Нажатый элемент
         */
        override fun onClick(v: View?) {
            /*
            notifyItemChanged Вызывает onBindViewHolder
            Воспользуемся этим для обновления цветного выделения выбранной даты
            */
            notifyItemChanged(focusedElement)
            focusedElement = adapterPosition // Запоминаем выбранный элемент
            notifyItemChanged(focusedElement)
            onNoteListener.onNoteClick(adapterPosition)
        }

        val dayNumber: TextView = itemView.findViewById(R.id.day)
        val dayOfWeek: TextView = itemView.findViewById(R.id.day_of_the_week)
        private val onNoteListener: OnNoteListener

        init {
            itemView.setOnClickListener(this)
            this.onNoteListener = onNoteListener
        }

    }

    /**
     * Возвращает число
     * @param date Дата
     */
    private fun getDayNumber(date: Date): String {
        val dateNumberFormat = SimpleDateFormat("d") // Шаблон для вывода числа
        return dateNumberFormat.format(date)
    }

    /**
     * Возвращает день недели
     * @param date Дата
     */
    private fun getDayOfWeek(date: Date): String {
        val dayOfWeekFormat = SimpleDateFormat("E") // Шаблон для вывода дня недели
        return dayOfWeekFormat.format(date)
    }

    /**
     * Интерфейс для обработки нажатий
     */
    interface OnNoteListener {
        /**
         * Абстрактная функция для обработки нажатий
         * @param position Позиция нажатого элемента
         */
        fun onNoteClick(position: Int)
    }


}