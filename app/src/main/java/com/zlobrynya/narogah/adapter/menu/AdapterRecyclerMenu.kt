package com.zlobrynya.narogah.adapter.menu

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.zlobrynya.narogah.R
import com.zlobrynya.narogah.activity.menu.FullDescriptionScreen
import com.zlobrynya.narogah.activity.menu.SubDescriptionScreen
import com.zlobrynya.narogah.tools.database.SubMenuDB
import com.zlobrynya.narogah.tools.retrofit.DTOs.menuDTOs.DishClientDTO
import kotlinx.android.synthetic.main.item_menu.view.*


/*
* Адаптер для RecyclerMenu отображение каточек блюда активити MenuActivity
 */

class AdapterRecyclerMenu(private val myDataset: ArrayList<DishClientDTO>, val context: Context): RecyclerView.Adapter<AdapterRecyclerMenu.Holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(com.zlobrynya.narogah.R.layout.item_menu, parent, false) as View
        return Holder(view)
    }

    override fun getItemCount(): Int = myDataset.size

    //Обновление текста
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(myDataset.get(position))
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
    //Класс помощник, для правильного отображение view
    inner class Holder(v: View) : RecyclerView.ViewHolder(v), View.OnClickListener {
        var idDish = 0
        var subDish = "null"
        var dish = DishClientDTO()
        @SuppressLint("SetTextI18n")
        fun bind(dishDTO: DishClientDTO) = with(itemView){
            //В класс помощник записываем данные
            Log.i("wtf2", dishDTO.toString())
            nameDish?.text = dishDTO.name.replace("\'", "\"")
            shortDescDish?.text = dishDTO.desc_short.replace("\'", "\"")
            if(shortDescDish?.text == "")shortDescDish.visibility = View.GONE
            idDish = dishDTO.item_id
            subDish = dishDTO.sub_menu
            dish = dishDTO
            Log.i("delivery", dishDTO.delivery)

            if(subDish != "null"){
                var listOfPrices: List<Int> = listOf()
                val subMenu = SubMenuDB(context)
                var minPrice = 9999
                val subDish = subMenu.getCategoryDish(dish.name)
                subDish.forEach {
                    listOfPrices = listOfPrices.plus(it.price)
                }
                listOfPrices = listOfPrices.distinct()
                if(listOfPrices.size > 1) {
                    subDish.forEach {
                        if (it.price < minPrice) minPrice = it.price
                    }
                    if (minPrice == 0) {
                        priceDish.text = ""
                    } else {
                        priceDish.text =
                            context.getString(R.string.from) + minPrice.toString() + context.getString(R.string.rub)
                    }
                }else{
                    priceDish.text = listOfPrices[0].toString() + context.getString(R.string.rub)
                }
                subMenu.closeDataBase()
            }else{
                if (dishDTO.price.toInt() == 0){
                    priceDish.text = ""
                }else {
                    priceDish.text = (dishDTO.price.toInt()).toString() + context.getString(R.string.rub)
                }
            }

            if (dishDTO.weight.contains("null")){
                vesDish.visibility = View.GONE
            } else{
                if(dishDTO.class_name == "НАПИТКИ"){
                    vesDish.text = dishDTO.weight + context.getString(R.string.ml)
                }else {
                    vesDish.text = dishDTO.weight + context.getString(R.string.gr)
                }
            }


            val myOptions = RequestOptions()
                .override(500, 400)
            //Glide
            Glide.with(this)
                .asBitmap()
                .apply(myOptions)
                .load(dishDTO.photo) // Изображение для теста. Исходное значение dishDTO.photo
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                //.skipMemoryCache(true)
                .placeholder(R.drawable.menu)
                .error(R.drawable.menu)
                .into(object: SimpleTarget<Bitmap>(){
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        progressBar.visibility = View.GONE
                        imageView.setImageBitmap(resource)
                    }
                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        super.onLoadFailed(errorDrawable)
                        progressBar.visibility = View.GONE
                        imageView.setImageDrawable(errorDrawable)
                    }
                })

            if (!(dishDTO.desc_long.isEmpty() && dishDTO.price.toInt() == 0
                    && dishDTO.weight == "null"))
                imageView!!.setOnClickListener(this@Holder)
        }

        override fun onClick(view: View) {
            Log.i("qq", dish.toString())
            if(subDish == "null") {
                Log.i("qq", subDish)
                val intent = Intent(context, FullDescriptionScreen::class.java)
                intent.putExtra(context.getString(R.string.key_id), idDish)
                context.startActivity(intent)
            }else{
                val intent = Intent(context, SubDescriptionScreen::class.java)
                intent.putExtra(context.getString(R.string.key_id), idDish)
                context.startActivity(intent)
            }
        }
    }
}