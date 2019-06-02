package com.zlobrynya.internshipzappa.adapter.booking

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import java.util.*
import android.widget.TextView
import com.zlobrynya.internshipzappa.R
import kotlinx.android.synthetic.main.item_booking_duration.view.*


/**
 * Адаптер для RecyclerView, отображающий календарные дни для брони
 */
class AdapterBookingDuration(
    private val values: ArrayList<BookDuration>,
    onDurationListener: OnDurationListener,
    context: Context?
) :
    RecyclerView.Adapter<AdapterBookingDuration.ViewHolder>() {

    /**
     * Номер выбранного элемента в выборе даты
     */
    var focusedElement: Int = 0

    private val mContext = context

    private val mOnDurationListener: OnDurationListener = onDurationListener

    /**
     * Принимает объект ViewHolder и устанавливает необходимые данные
     * @param holder ViewHolder
     * @param position Позиция элемента
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // По необходимости меняем цветовую обводку у квадратика с датой
        if (position == focusedElement) {
            holder.itemView.setBackgroundResource(R.drawable.item_day_selected_shape)
            if (mContext != null) {
                holder.itemView.duration_text_view.setTextColor(mContext.resources.getColor(R.color.white))
            }
        } else {
            holder.itemView.setBackgroundResource(R.drawable.item_day_shape)
            if (mContext != null) {
                holder.itemView.duration_text_view.setTextColor(mContext.resources.getColor(R.color.text_tab_bar_not_select))
            }
        }

        holder.duration.text = values[position].time

        // Скрываем варианты длительности брони по необходимости
        if (!values[position].isVisible) holder.itemView.visibility = View.INVISIBLE
        else holder.itemView.visibility = View.VISIBLE

    }

    /**
     * Создает новый объект ViewHolder
     * @param ViewType тип нужного ViewHolder
     */
    override fun onCreateViewHolder(parent: ViewGroup, ViewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_booking_duration, parent, false)
        return ViewHolder(itemView, mOnDurationListener)
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
    inner class ViewHolder(itemView: View, onDurationListener: OnDurationListener) : RecyclerView.ViewHolder(itemView),
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
            onDurationListener.onDurationClick(adapterPosition)

        }

        val duration: TextView = itemView.findViewById(R.id.duration_text_view)
        private val onDurationListener: OnDurationListener

        init {
            itemView.setOnClickListener(this)
            this.onDurationListener = onDurationListener
        }
    }

    /**
     * Интерфейс для обработки нажатий вне адаптера
     */
    interface OnDurationListener {
        /**
         * Абстрактная функция для обработки нажатий
         * @param position Позиция нажатого элемента
         */
        fun onDurationClick(position: Int)
    }
}