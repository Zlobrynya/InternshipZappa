package com.zlobrynya.internshipzappa.activity

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.assist.FailReason
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener
import com.zlobrynya.internshipzappa.adapter.AdapterRecommendDish
import com.zlobrynya.internshipzappa.R
import com.zlobrynya.internshipzappa.tools.database.MenuDB
import com.zlobrynya.internshipzappa.tools.retrofit.dto.DishDTO
import kotlinx.android.synthetic.main.activity_full_description_screen.*
import android.view.MenuItem
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.nostra13.universalimageloader.cache.disc.impl.LimitedAgeDiskCache
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator
import com.nostra13.universalimageloader.core.DisplayImageOptions
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration
import com.nostra13.universalimageloader.core.assist.ImageScaleType
import com.nostra13.universalimageloader.core.download.BaseImageDownloader
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_rect_recommend_dish.view.*
import java.lang.Exception
import java.security.AccessController.getContext
import java.util.*


class FullDescriptionScreen : AppCompatActivity() {

    private lateinit var menuDB: MenuDB



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_description_screen)
        //ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(this@FullDescriptionScreen))

        val intent = intent
        val id = intent.getIntExtra(getString(R.string.key_id),0)

        //ToolBar
        toolbar.setBackgroundColor(resources.getColor(R.color.black_alpha_40))
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowTitleEnabled(false)

        //что бы при открытии скрол был наверху, а не так что бы при открытии активити оказывались по середине
         scrollView.smoothScrollTo(0,0)

        //Создание RecyclerView
        val layoutManager = LinearLayoutManager(this@FullDescriptionScreen, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.layoutManager = layoutManager

        menuDB = MenuDB(this)
        val dish = menuDB.getDescriptionDish(id)
        showDishDescription(dish)

        val array = listRecDish(dish.recommended)
        if (!array.isEmpty())
            recyclerView.adapter = AdapterRecommendDish(listRecDish(dish.recommended))
    }

    @SuppressLint("SetTextI18n")
    fun showDishDescription(dish: DishDTO){
        //Проверка получены ли данные
        dishCena.text = if (dish.price == 0.0) getString(R.string.munis) else (dish.price.toInt()).toString() + getString(R.string.rub)

        if (dish.weight == ""){
            dishVes.visibility = View.GONE
        } else{
            dishVes.text = dish.weight + getString(R.string.gr)
        }

        dishName.text = dish.name

        if (dish.desc_long.isEmpty()){
            dishOpisanie.visibility = View.GONE
        } else{
            dishOpisanie.text = dish.desc_long
        }

        //Glide
        Glide.with(this@FullDescriptionScreen)
            .asBitmap()
            .load("https://na-rogah-api.herokuapp.com/api/v1/photos/d780d09jpg") // Изображение для теста. Исходное значение dish.photo
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .placeholder(R.drawable.menu)
            .into(object: SimpleTarget<Bitmap>(){
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    progressBar.visibility = View.GONE
                    dishPhoto.setImageBitmap(resource)
                }

            })
    }

    fun listRecDish(str: String): ArrayList<DishDTO> {
        val list: ArrayList<DishDTO> = ArrayList()
        if (!str.isEmpty()){
            val delimiter = ';'
            val parts = str.split(delimiter)
            for (i in 0 until parts.size){
                if (!parts.get(i).contains("null"))
                    list.add(menuDB.getDescriptionDish(parts.get(i).toInt()))
            }
            if (list.size == 0)
                textView7.visibility = View.GONE
        }
        return list
    }

    override fun onDestroy() {
        menuDB.closeDataBase()
        super.onDestroy()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            android.R.id.home -> {
                this.finish()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }


    override fun onResume() {
        super.onResume()

        if (!ImageLoader.getInstance().isInited){
            val options = DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
                .build()

            val config = ImageLoaderConfiguration.Builder(this)
                .threadPoolSize(3)
                .diskCache(LimitedAgeDiskCache(cacheDir, null, HashCodeFileNameGenerator(), (60 * 30).toLong()))
                .imageDownloader(BaseImageDownloader(this)) // connectTimeout (5 s), readTimeout (30 s)
                .defaultDisplayImageOptions(options)
                .build()
            ImageLoader.getInstance().init(config)
        }
    }
}