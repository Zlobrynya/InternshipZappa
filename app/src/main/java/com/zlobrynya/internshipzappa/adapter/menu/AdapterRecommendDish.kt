package com.zlobrynya.internshipzappa.adapter.menu

import android.annotation.SuppressLint
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.zlobrynya.internshipzappa.R
import kotlinx.android.synthetic.main.item_rect_recommend_dish.view.*
import android.graphics.*
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.provider.Settings.System.getString

import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.zlobrynya.internshipzappa.tools.database.SubMenuDB
import com.zlobrynya.internshipzappa.tools.retrofit.DTOs.menuDTOs.DishClientDTO
import kotlinx.android.synthetic.main.item_menu.view.*


/*
* Адаптер для RecyclerView рекомендованных блюд на экране FullDescriptionScreen
* */
class AdapterRecommendDish(private val values: ArrayList<DishClientDTO>): RecyclerView.Adapter<AdapterRecommendDish.ViewHolder>() {

    override fun getItemCount() = values!!.size

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_rect_recommend_dish, parent, false)

        return ViewHolder(itemView)
    }


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(values.get(position))
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        // Установка данных в view
        @SuppressLint("SetTextI18n")
        fun bind(dishDTO: DishClientDTO) = with(itemView){
            val subDish = dishDTO.sub_menu

            if(subDish != "null"){
                val subMenu = SubMenuDB(context)
                var minPrice = 9999
                val subDish = subMenu.getCategoryDish(dishDTO.name)
                subDish.forEach {
                    if (it.price < minPrice) minPrice = it.price
                }
                if (minPrice == 0){
                    topingPrice.text = ""
                }else {
                    topingPrice.text =
                        context.getString(R.string.from) + minPrice.toString() + context.getString(R.string.rub)
                }
                subMenu.closeDataBase()
            }else{
                if (dishDTO.price.toInt() == 0){
                    topingPrice.text = ""
                }else {
                    topingPrice.text = (dishDTO.price.toInt()).toString() + context.getString(R.string.rub)
                }
            }

            if (dishDTO.weight.contains("null")){
                topingVes.visibility = View.GONE
            } else{
                if(dishDTO.class_name == "НАПИТКИ"){
                    topingVes.text = dishDTO.weight + context.getString(R.string.ml)
                }else {
                    topingVes.text = dishDTO.weight + context.getString(R.string.gr)
                }
            }

            topingName.text = dishDTO.name.replace("\'", "\"")

            //Glide
            Glide.with(context)
                .asBitmap()
                .load(dishDTO.photo) // Изображение для теста. Исходное значение dishDTO.photo
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                //.skipMemoryCache(true)
                .placeholder(R.drawable.no_toping)
                .error(R.drawable.no_toping)
                .into(object:SimpleTarget<Bitmap>(){
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        progressBar2.visibility = View.GONE
                        topingPhoto.setImageBitmap(resource)
                    }

                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        super.onLoadFailed(errorDrawable)
                        progressBar2.visibility = View.GONE
                        topingPhoto.setImageDrawable(errorDrawable)
                    }
                })
        }
    }
}