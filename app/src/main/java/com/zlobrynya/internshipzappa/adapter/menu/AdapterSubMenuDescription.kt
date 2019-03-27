package com.zlobrynya.internshipzappa.adapter.menu

import android.annotation.SuppressLint
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zlobrynya.internshipzappa.R
import com.zlobrynya.internshipzappa.tools.retrofit.DTOs.menuDTOs.DishSubDTO
import kotlinx.android.synthetic.main.item_sub_menu.view.*


/*
* Адаптер для RecyclerView рекомендованных блюд на экране FullDescriptionScreen
* */
class AdapterSubMenuDescription(private val values: ArrayList<DishSubDTO>): RecyclerView.Adapter<AdapterSubMenuDescription.ViewHolder>() {

    override fun getItemCount() = values!!.size

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_sub_menu, parent, false)

        return ViewHolder(itemView)
    }


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(values.get(position))
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        // Установка данных в view
        @SuppressLint("SetTextI18n")
        fun bind(dishDTO: DishSubDTO) = with(itemView) {
            sub_price.text = if (dishDTO.price == 0) "-"
            else dishDTO.price.toString() + context.getString(R.string.rub)
            sub_name_dish.text = dishDTO.name.replace("\'", "\"")

            if (dishDTO.weight.contains("null")) {
                sub_name_dish.text = dishDTO.name.replace("\'", "\"")
            } else {
                sub_name_dish.text = dishDTO.name.replace("\'", "\"") +
                        ", " + dishDTO.weight + context.getString(R.string.ml)
            }

        }
    }
}