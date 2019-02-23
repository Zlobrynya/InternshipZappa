package com.zlobrynya.internshipzappa.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.support.design.widget.AppBarLayout
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.zlobrynya.internshipzappa.R
import android.view.LayoutInflater
import android.widget.ProgressBar
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration
import com.zlobrynya.internshipzappa.tools.ImageDishUIL
//import com.zlobrynya.internshipzappa.tools.DishImageView
import com.zlobrynya.internshipzappa.tools.parcelable.Dish


/*
* Адаптер для RecyclerMenu
 */

class AdapterRecyclerMenu(private val myDataset: ArrayList<Dish>, val context: Context): RecyclerView.Adapter<AdapterRecyclerMenu.Holder>() {
    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_menu, parent, false) as View
        return Holder(view)
    }

    override fun getItemCount(): Int {
        return myDataset.size
    }

    //Обновление текста
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: Holder, position: Int) {
        //В класс помощник записываем данные
        holder.nameDish?.text = myDataset[position].name
        holder.descDish?.text = myDataset[position].descr
        holder.weightDish!!.text = myDataset[position].weight
        holder.priceDish!!.text = myDataset[position].price.toString() + " руб."
        holder.imageView?.uilImage(myDataset[position].pathImage)
        //Проверка на то, загружено ли в imageView изображение
      /*if (!holder.imageView!!.isImageLoad) {
            holder.imageView?.progressBar = holder.progressBar
            holder.imageView?.textView = holder.nameDish
            holder.imageView?.setURL(myDataset[position].pathImage)
        }*/
    }

    //Класс помощник, для правильного отображение view
    class Holder(v: View) : RecyclerView.ViewHolder(v), View.OnClickListener{
        var nameDish: TextView? = null
        var imageView: ImageDishUIL? = null
        var descDish: TextView? = null
        var priceDish: TextView? = null
        var weightDish: TextView? = null
        var shapeDish: CardView? = null
        var progressBar: ProgressBar? = null



        init {
            nameDish = v.findViewById(R.id.nameDish)
            imageView = v.findViewById(R.id.imageView)
            descDish = v.findViewById(R.id.descDish)
            priceDish = v.findViewById(R.id.priceDish)
            weightDish = v.findViewById(R.id.weightDish)
            shapeDish = v.findViewById(R.id.shapeDish)
            progressBar = v.findViewById(R.id.progressBar)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                imageView!!.clipToOutline = true
            }
            imageView!!.setOnClickListener(this)

        }

        override fun onClick(view: View) {
            Log.d("Click", "onClick $position")

            //при нажатии на изображение скрываем или показываем карточки с подробным описанием
            if (shapeDish!!.isShown){
                shapeDish!!.visibility = View.GONE
            }else{
                shapeDish!!.visibility = View.VISIBLE
            }
        }
    }
}