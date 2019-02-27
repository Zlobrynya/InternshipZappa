package com.zlobrynya.internshipzappa.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.zlobrynya.internshipzappa.R
import android.view.LayoutInflater
import android.widget.ProgressBar
import com.nostra13.universalimageloader.core.ImageLoader
import com.zlobrynya.internshipzappa.tools.retrofit.dto.DishDTO
import android.graphics.Bitmap
import android.support.v7.widget.CardView
import com.nostra13.universalimageloader.core.DisplayImageOptions
import com.nostra13.universalimageloader.core.assist.FailReason
import com.nostra13.universalimageloader.core.assist.ImageScaleType
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener
import com.zlobrynya.internshipzappa.activity.FullDescriptionScreen


/*
* Адаптер для RecyclerMenu
 */

class AdapterRecyclerMenu(private val myDataset: ArrayList<DishDTO>, val context: Context): RecyclerView.Adapter<AdapterRecyclerMenu.Holder>() {

    lateinit var options: DisplayImageOptions


    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_menu, parent, false) as View

        /*опции UIL
        * кэширует в память
        * */
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
        //В класс помощник записываем данные
        holder.nameDish?.text = myDataset[position].name
        holder.descDish?.text = myDataset[position].desc_short
        holder.id = myDataset[position].item_id
        holder.priceDish!!.text = myDataset[position].price.toInt().toString() + " \u20BD"
        //загрузка изображений
        val imageLoader: ImageLoader = ImageLoader.getInstance()
        imageLoader.displayImage(myDataset[position].photo, holder.imageView, options, object: ImageLoadingListener {
            override fun onLoadingComplete(imageUri: String?, view: View?, loadedImage: Bitmap?) {
                holder.progressBar!!.visibility = View.GONE
            }

            override fun onLoadingStarted(imageUri: String?, view: View?) {

            }

            override fun onLoadingCancelled(imageUri: String?, view: View?) {
                holder.progressBar!!.visibility = View.GONE
                imageLoader.displayImage("drawable://"+ R.drawable.no_in_menu, holder.imageView)
            }

            override fun onLoadingFailed(imageUri: String?, view: View?, failReason: FailReason?) {
                holder.progressBar!!.visibility = View.GONE
                imageLoader.displayImage("drawable://"+ R.drawable.no_in_menu, holder.imageView)
            }

        })
    }

    //Класс помощник, для правильного отображение view
    inner class Holder(v: View) : RecyclerView.ViewHolder(v), View.OnClickListener {
        var nameDish: TextView? = null
        var imageView: ImageView? = null
        var descDish: TextView? = null
        var priceDish: TextView? = null
        var progressBar: ProgressBar? = null
        var id = 0

        init {
            nameDish = v.findViewById(R.id.nameDish)
            imageView = v.findViewById(R.id.imageView)
            descDish = v.findViewById(R.id.shortDescDish)
            priceDish = v.findViewById(R.id.priceDish)
            progressBar = v.findViewById(R.id.progressBar)
            imageView!!.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            //тут будет старт view Ильи и передача id intent'ом
            val intent = Intent(context, FullDescriptionScreen::class.java)
            intent.putExtra("id", id)
            context.startActivity(intent)
        }
    }
}