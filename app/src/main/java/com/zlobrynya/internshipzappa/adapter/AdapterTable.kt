package com.zlobrynya.internshipzappa.adapter

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.zlobrynya.internshipzappa.R
import kotlinx.android.synthetic.main.item_table.view.*

/**
 * Адаптер для вывода столиков для брони
 * (Возможно изменится после выхода сервера)
 */
class AdapterTable(private val values: ArrayList<Table>) : RecyclerView.Adapter<AdapterTable.ViewHolder>() {

    /**
     * Устанавливает в ViewHolder нужные данные
     * @param holder Экземпляр ViewHolder
     * @param position Позиция элемента
     */
    override fun onBindViewHolder(holder: AdapterTable.ViewHolder, position: Int) {
        holder.seatType.text = values[position].seatType
        holder.seatCount.text = values[position].seatCount
        holder.choseButton.setOnClickListener {
            Log.d("TOPKEK", "Выбран столик с айди $position") //Тут должен быть переход дальше или что-то такое
        }
    }

    /**
     * Создает нужный ViewHolder
     */
    override fun onCreateViewHolder(parent: ViewGroup, ViewType: Int): AdapterTable.ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_table, parent, false)
        return ViewHolder(itemView)
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
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val seatType: TextView = itemView.seat_type
        val seatCount: TextView = itemView.seat_count
        val choseButton: Button = itemView.chose_button
    }
}