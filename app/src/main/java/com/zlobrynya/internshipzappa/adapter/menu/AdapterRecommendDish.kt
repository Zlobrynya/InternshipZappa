package com.zlobrynya.internshipzappa.adapter.menu

import android.annotation.SuppressLint
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.zlobrynya.internshipzappa.R
import com.zlobrynya.internshipzappa.tools.retrofit.DTOs.menuDTOs.DishDTO
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


/*
* Адаптер для RecyclerView рекомендованных блюд на экране FullDescriptionScreen
* */
class AdapterRecommendDish(private val values: ArrayList<DishDTO>): RecyclerView.Adapter<AdapterRecommendDish.ViewHolder>() {

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
        fun bind(dishDTO: DishDTO) = with(itemView){
            topingPrice.text = if (dishDTO.price == 0.0) "-"
            else dishDTO.price.toInt().toString() + context.getString(R.string.rub)

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
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
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

            /*btnToping.setOnClickListener(this@ViewHolder)
            btnPlus.setOnClickListener(this@ViewHolder)
            btnMinus.setOnClickListener(this@ViewHolder)*/


        }

        /*override fun onClick(view: View) = with(itemView) {
            when (view){
                btnToping -> {
                    btnToping.visibility = View.GONE
                    btnPlus.visibility = View.VISIBLE
                    btnMinus.visibility = View.VISIBLE
                    tvCounter.visibility = View.VISIBLE
                }
                btnPlus -> {
                    tvCounter.text = (tvCounter.text.toString().toInt() + 1).toString()
                    if (tvCounter.text == context.getString(R.string.max_order))
                        btnPlus.visibility = View.GONE
                }
                btnMinus -> {
                    tvCounter.text = (tvCounter.text.toString().toInt() - 1).toString()
                    when (tvCounter.text){
                        context.getString(R.string.zero_num) ->{
                            btnToping.visibility = View.VISIBLE
                            btnPlus.visibility = View.GONE
                            btnMinus.visibility = View.GONE
                            tvCounter.visibility = View.GONE
                            tvCounter.text = context.getString(R.string.default_num)
                            Toast.makeText(itemView.context, context.getString(R.string.topping_delete), Toast.LENGTH_SHORT).show()
                        }
                        (context.getString(R.string.max_order).toInt() - 1).toString() ->{
                            btnPlus.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }*/
    }
}