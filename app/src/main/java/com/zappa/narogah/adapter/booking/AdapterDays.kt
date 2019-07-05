package com.zappa.narogah.adapter.booking

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.zappa.narogah.R
import kotlinx.android.synthetic.main.item_day.view.*
import java.text.SimpleDateFormat
import java.util.*


/**
 * Адаптер для RecyclerView, отображающий календарные дни для брони
 */
class AdapterDays(private val values: ArrayList<Date>, onDateListener: OnDateListener, context: Context?) :
    RecyclerView.Adapter<AdapterDays.ViewHolder>() {

    /**
     * Номер выбранного элемента в выборе даты
     */
    var focusedElement: Int = 0

    private val mContext = context

    private val mOnDateListener: OnDateListener = onDateListener

    /**
     * Принимает объект ViewHolder и устанавливает необходимые данные
     * @param holder ViewHolder
     * @param position Позиция элемента
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.i("checkDateFrom1", values.toString())

        holder.dayNumber.text = getDayNumber(values[position])
        holder.dayOfWeek.text = getDayOfWeek(values[position])

        // По необходимости меняем цветовую обводку у квадратика с датой
        if (position == focusedElement) {
            holder.itemView.setBackgroundResource(R.drawable.item_day_selected_shape)
            if (mContext != null) {
                holder.itemView.day.setTextColor(mContext.resources.getColor(R.color.white))
                holder.itemView.day_of_the_week.setTextColor(mContext.resources.getColor(R.color.white))
            }

        } else {
            holder.itemView.setBackgroundResource(R.drawable.item_day_shape)
            if (mContext != null) {
                holder.itemView.day.setTextColor(mContext.resources.getColor(R.color.text_tab_bar_not_select))
                holder.itemView.day_of_the_week.setTextColor(mContext.resources.getColor(R.color.text_tab_bar_not_select))
            }
        }
    }

    /**
     * Создает новый объект ViewHolder
     * @param ViewType тип нужного ViewHolder
     */
    override fun onCreateViewHolder(parent: ViewGroup, ViewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_day, parent, false)
        return ViewHolder(itemView, mOnDateListener)
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
    inner class ViewHolder(itemView: View, onDateListener: OnDateListener) : RecyclerView.ViewHolder(itemView),
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
            onDateListener.onDateClick(adapterPosition)
        }

        val dayNumber: TextView = itemView.findViewById(R.id.day)
        val dayOfWeek: TextView = itemView.findViewById(R.id.day_of_the_week)
        private val onDateListener: OnDateListener

        init {
            itemView.setOnClickListener(this)
            this.onDateListener = onDateListener
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
     * Интерфейс для обработки нажатий вне адаптера
     */
    interface OnDateListener {
        /**
         * Абстрактная функция для обработки нажатий
         * @param position Позиция нажатого элемента
         */
        fun onDateClick(position: Int)
    }
}