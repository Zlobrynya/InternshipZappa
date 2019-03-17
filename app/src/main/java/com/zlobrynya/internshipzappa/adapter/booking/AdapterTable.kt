package com.zlobrynya.internshipzappa.adapter.booking

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import kotlinx.android.synthetic.main.item_table.view.*


/**
 * Адаптер для вывода столиков для брони
 * (Возможно изменится после выхода сервера)
 */
class AdapterTable(private val values: ArrayList<Table>, onTableListener: OnTableListener) :
    RecyclerView.Adapter<AdapterTable.ViewHolder>() {

    private val mOnTableListener = onTableListener

    /**
     * Устанавливает в ViewHolder нужные данные
     * @param holder Экземпляр ViewHolder
     * @param position Позиция элемента
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.seatType.text = values[position].seatType
        //обрабатываем падежы
        if (values[position].seatPosition == ""){
            when(values[position].seatCount){
             4-> holder.seatCount.text = "${values[position].seatCount} места"
             else -> holder.seatCount.text = "${values[position].seatCount} мест"
            }
        }
        else{
            when(values[position].seatCount){
                4-> holder.seatCount.text = "${values[position].seatCount} места, ${values[position].seatPosition}"
                else -> holder.seatCount.text = "${values[position].seatCount} мест, ${values[position].seatPosition}"
            }
        }
        holder.choseButton.setOnClickListener {
            holder.onTableListener.onTableClick(position, true)
        }
    }

    /**
     * Создает нужный ViewHolder
     */
    override fun onCreateViewHolder(parent: ViewGroup, ViewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(com.zlobrynya.internshipzappa.R.layout.item_table, parent, false)
        return ViewHolder(itemView, mOnTableListener)
    }

    /**
     * Размер коллекции
     */
    override fun getItemCount(): Int {
        return values.size
    }

    /**
     * ViewHolder для информации о столике
     */
    inner class ViewHolder(itemView: View, onTableListener: OnTableListener) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        override fun onClick(v: View?) {
            onTableListener.onTableClick(adapterPosition, false)
        }

        val seatType: TextView = itemView.seat_type
        val seatCount: TextView = itemView.seat_count
        val choseButton: Button = itemView.chose_button
        val onTableListener: OnTableListener

        init {
            itemView.setOnClickListener(this)
            this.onTableListener = onTableListener
        }
    }

    /**
     * Интерфейс для обработки нажатий вне адаптера
     */
    interface OnTableListener {
        /**
         * Абстрактная функция для обработки нажатий
         * @param position Позиция нажатого элемента
         * @param isButtonClick Произошло ли нажатие на кнопку "выбрать"
         */
        fun onTableClick(position: Int, isButtonClick: Boolean)
    }
}