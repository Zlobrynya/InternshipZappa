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
import com.nostra13.universalimageloader.core.DisplayImageOptions
import com.nostra13.universalimageloader.core.assist.FailReason
import com.nostra13.universalimageloader.core.assist.ImageScaleType
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener
import com.zlobrynya.internshipzappa.activity.FullDescriptionScreen
import kotlinx.android.synthetic.main.item_menu.view.*


/*
* Адаптер для RecyclerMenu отображение каточек блюда активити MenuActivity
 */

class AdapterRecyclerMenu(private val myDataset: ArrayList<DishDTO>, val context: Context): RecyclerView.Adapter<AdapterRecyclerMenu.Holder>() {

    lateinit var options: DisplayImageOptions


    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_menu, parent, false) as View

        //опции UIL кэширует в память
        options = DisplayImageOptions.Builder()
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
            .build()

        return Holder(view)
    }

    override fun getItemCount(): Int = myDataset.size

    //Обновление текста
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(myDataset.get(position))
    }

    //Класс помощник, для правильного отображение view
    inner class Holder(v: View) : RecyclerView.ViewHolder(v), View.OnClickListener {
        var id = 0

        @SuppressLint("SetTextI18n")
        fun bind(dishDTO: DishDTO) = with(itemView){
            //В класс помощник записываем данные
            nameDish?.text = dishDTO.name
            shortDescDish?.text = dishDTO.desc_short
            id = dishDTO.item_id
            priceDish!!.text = dishDTO.price.toInt().toString() + context.getString(R.string.rub)

            //загрузка изображений
            val imageLoader: ImageLoader = ImageLoader.getInstance()
            imageLoader.displayImage(dishDTO.photo, imageView, options, object: ImageLoadingListener {
                override fun onLoadingComplete(imageUri: String?, view: View?, loadedImage: Bitmap?) {
                    progressBar!!.visibility = View.GONE
                }

                override fun onLoadingStarted(imageUri: String?, view: View?) {

                }

                override fun onLoadingCancelled(imageUri: String?, view: View?) {
                    progressBar!!.visibility = View.GONE
                    imageLoader.displayImage("drawable://"+ R.drawable.no_in_menu, imageView)
                }

                override fun onLoadingFailed(imageUri: String?, view: View?, failReason: FailReason?) {
                    progressBar!!.visibility = View.GONE
                    imageLoader.displayImage("drawable://"+ R.drawable.no_in_menu, imageView)
                }
            })
            imageView!!.setOnClickListener(this@Holder)
        }

        override fun onClick(view: View) {
            //тут будет старт view Ильи и передача id intent'ом
            val intent = Intent(context, FullDescriptionScreen::class.java)
            intent.putExtra("id", id)
            context.startActivity(intent)
        }
    }
}