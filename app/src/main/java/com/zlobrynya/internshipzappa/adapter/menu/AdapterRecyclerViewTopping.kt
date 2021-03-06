package com.zlobrynya.internshipzappa.adapter.menu

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.view.ViewGroup
import com.zlobrynya.internshipzappa.R
import android.view.LayoutInflater
import com.zlobrynya.internshipzappa.tools.retrofit.DTOs.menuDTOs.DishClientDTO
import kotlinx.android.synthetic.main.item_topping_menu.view.*


/*
* Адаптер для отобрадежения топингов и напитков RecyclerMenu
*/
class AdapterRecyclerViewTopping(private val myDataset: ArrayList<DishClientDTO>, val context: Context):
    RecyclerView.Adapter<AdapterRecyclerViewTopping.Holder>() {


    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_topping_menu, parent, false) as View
        return Holder(view)
    }

    override fun getItemCount(): Int {
        return myDataset.size
    }

    //Обновление текста
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(myDataset.get(position))
    }


    //Класс помощник, для правильного отображение view для топпингов
    class Holder(v: View) : RecyclerView.ViewHolder(v), View.OnClickListener {


        @SuppressLint("SetTextI18n")
        fun bind(dishDTO: DishClientDTO) = with(itemView){
            //В класс помощник записываем данные
            nameTopping.text = dishDTO.name
            Log.i("cat", dishDTO.class_name)
            if (dishDTO.weight == "null") weightTopping.visibility = View.GONE
            else if (dishDTO.class_name == "Напитки") weightTopping.text = dishDTO.weight + " мл"
            else weightTopping.text = dishDTO.weight + " г"
            priceTopping.text = dishDTO.price.toInt().toString() + context.getString(R.string.rub)

            //TODO на следующий спринт
            addButton.visibility = View.GONE
            /*addButton!!.setOnClickListener(this@Holder)
            plusButton!!.setOnClickListener(this@Holder)
            minusButton!!.setOnClickListener(this@Holder)*/
        }

        override fun onClick(view: View) = with(itemView){
            when (view){
                addButton -> {
                    view.visibility = View.GONE
                    counterOn!!.visibility = View.VISIBLE
                }
                plusButton -> {
                    counter!!.text = (counter!!.text.toString().toInt() + 1).toString()
                    if(counter!!.text == context.getString(R.string.max_order)){
                        plusButton!!.visibility = View.GONE
                    }
                }
                minusButton -> {
                    counter!!.text = (counter!!.text.toString().toInt() - 1).toString()
                    when(counter.text){
                        context.getString(R.string.zero_num) ->{
                            counterOn!!.visibility = View.GONE
                            addButton!!.visibility = View.VISIBLE
                            counter!!.text = context.getString(R.string.default_num)
                        }
                        (context.getString(R.string.max_order).toInt() - 1).toString() ->{
                            plusButton!!.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }
    }
}