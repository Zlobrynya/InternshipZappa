package com.zlobrynya.internshipzappa.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.zlobrynya.internshipzappa.R
import android.view.LayoutInflater
import com.nostra13.universalimageloader.core.ImageLoader
import com.zlobrynya.internshipzappa.tools.retrofit.dto.DishDTO
import android.graphics.Bitmap
import android.support.annotation.IntegerRes
import android.util.Log
import android.widget.*
import com.nostra13.universalimageloader.core.DisplayImageOptions
import com.nostra13.universalimageloader.core.assist.FailReason
import com.nostra13.universalimageloader.core.assist.ImageScaleType
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener


/*
* Адаптер для отобрадежения топингов и напитков
* RecyclerMenu
*
 */

class AdapterRecyclerViewTopping(private val myDataset: ArrayList<DishDTO>, val context: Context):
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
        //В класс помощник записываем данные
        holder.nameTop?.text = myDataset[position].name
        holder.weightTop?.text = myDataset[position].weight.toString() + " г"
        holder.priceTop!!.text = myDataset[position].price.toString() + " \u20BD"
    }


    //Класс помощник, для правильного отображение view для топпингов
    class Holder(v: View) : RecyclerView.ViewHolder(v), View.OnClickListener {
        var nameTop: TextView? = null
        var weightTop: TextView? = null
        var priceTop: TextView? = null
        var addButton: Button? = null
        var counterOn: FrameLayout? = null
        var counter: TextView? = null
        var minusButton: Button? = null
        var plusButton: Button? = null

        init {
            nameTop = v.findViewById(R.id.nameTopping)
            weightTop = v.findViewById(R.id.weightTopping)
            priceTop = v.findViewById(R.id.priceTopping)
            counterOn = v.findViewById(R.id.counterOn)
            addButton = v.findViewById(R.id.addButton)
            counter = v.findViewById(R.id.counter)
            plusButton = v.findViewById(R.id.plusButton)
            minusButton = v.findViewById(R.id.minusButton)
            addButton!!.setOnClickListener(this)
            plusButton!!.setOnClickListener(this)
            minusButton!!.setOnClickListener(this)

        }

        override fun onClick(view: View) {
            //ухх наговнокодил-_-
            if (view == addButton) {
                view.visibility = View.GONE
                counterOn!!.visibility = View.VISIBLE
            }

            if (view == plusButton) {
                counter!!.text = (counter!!.text.toString().toInt() + 1).toString()
                if(counter!!.text == "15"){
                    plusButton!!.visibility = View.GONE
                }
            }

            if (view == minusButton) {
                counter!!.text = (counter!!.text.toString().toInt() - 1).toString()
                if (counter!!.text == "0") {
                    counterOn!!.visibility = View.GONE
                    addButton!!.visibility = View.VISIBLE
                    counter!!.text = "1"
                }else if (counter!!.text == "14"){
                    plusButton!!.visibility = View.VISIBLE
                }
            }
        }
    }
}