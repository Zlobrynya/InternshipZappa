package com.zlobrynya.internshipzappa.adapter.menu

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.zlobrynya.internshipzappa.R
import android.view.LayoutInflater
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.Log
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.zlobrynya.internshipzappa.activity.menu.FullDescriptionScreen
import com.zlobrynya.internshipzappa.activity.menu.SubDescriptionScreen
import com.zlobrynya.internshipzappa.tools.retrofit.DTOs.menuDTOs.DishClientDTO
import kotlinx.android.synthetic.main.item_menu.view.*

/*
* Адаптер для RecyclerMenu отображение каточек блюда активити MenuActivity
 */

class AdapterRecyclerMenu(private val myDataset: ArrayList<DishClientDTO>, val context: Context): RecyclerView.Adapter<AdapterRecyclerMenu.Holder>() {

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
            priceDish.text = if (dishDTO.price.toInt() == 0) ""
                else (dishDTO.price.toInt()).toString() + context.getString(R.string.rub)

            if (dishDTO.weight.contains("null")){
                vesDish.visibility = View.GONE
            } else{
                if(dishDTO.class_name == "НАПИТКИ"){
                    vesDish.text = dishDTO.weight + context.getString(R.string.ml)
                }else {
                    vesDish.text = dishDTO.weight + context.getString(R.string.gr)
                }
            }

            //Glide
            Glide.with(this)
                .asBitmap()
                .load(dishDTO.photo) // Изображение для теста. Исходное значение dishDTO.photo
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
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