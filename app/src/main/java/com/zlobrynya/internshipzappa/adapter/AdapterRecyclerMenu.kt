package com.zlobrynya.internshipzappa.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.zlobrynya.internshipzappa.R
import android.view.LayoutInflater
import com.nostra13.universalimageloader.core.ImageLoader
import com.zlobrynya.internshipzappa.tools.retrofit.dto.DishDTO
import android.graphics.Bitmap
import android.util.Log
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.nostra13.universalimageloader.core.assist.FailReason
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener
import com.squareup.picasso.Picasso
import com.zlobrynya.internshipzappa.activity.FullDescriptionScreen
import kotlinx.android.synthetic.main.activity_full_description_screen.*
import kotlinx.android.synthetic.main.item_menu.view.*
import kotlinx.android.synthetic.main.item_rect_recommend_dish.view.*
import java.lang.Exception


/*
* Адаптер для RecyclerMenu отображение каточек блюда активити MenuActivity
 */

class AdapterRecyclerMenu(private val myDataset: ArrayList<DishDTO>, val context: Context): RecyclerView.Adapter<AdapterRecyclerMenu.Holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(com.zlobrynya.internshipzappa.R.layout.item_menu, parent, false) as View
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

        @SuppressLint("SetTextI18n")
        fun bind(dishDTO: DishDTO) = with(itemView){
            //В класс помощник записываем данные
            nameDish?.text = dishDTO.name
            shortDescDish?.text = dishDTO.desc_short
            idDish = dishDTO.item_id
            Log.i("delivery", dishDTO.delivery)
            priceDish.text = if (dishDTO.price.toInt() == 0) context.getString(R.string.munis)
                else (dishDTO.price.toInt()).toString() + context.getString(R.string.rub)

            //Glide
            Glide.with(this)
                .asBitmap()
                .load(dishDTO.photo) // Изображение для теста. Исходное значение dishDTO.photo
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.menu)
                .into(object: SimpleTarget<Bitmap>(){
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        progressBar.visibility = View.GONE
                        imageView.setImageBitmap(resource)
                    }

                })

            if (!(dishDTO.desc_long.isEmpty() && dishDTO.price.toInt() == 0
                    && dishDTO.weight == "null"))
                imageView!!.setOnClickListener(this@Holder)
        }

        override fun onClick(view: View) {
            val intent = Intent(context, FullDescriptionScreen::class.java)
            intent.putExtra(context.getString(R.string.key_id), idDish)
            context.startActivity(intent)
        }
    }
}