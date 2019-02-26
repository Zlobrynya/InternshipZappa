package com.zlobrynya.internshipzappa

import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.ProgressBar
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.assist.FailReason
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener
import com.zlobrynya.internshipzappa.database.MenuDB
import com.zlobrynya.internshipzappa.tools.DescriptionDish
import kotlinx.android.synthetic.main.full_description_screen.*
import kotlinx.android.synthetic.main.rect_recommend_dish.*

class FullDescriptionScreen : AppCompatActivity() {

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.full_description_screen)

        //ToolBar
        var toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.setBackgroundColor(resources.getColor(R.color.black_alpha_40))
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)

        //Создание RecyclerView
        val layoutManager = LinearLayoutManager(this@FullDescriptionScreen, LinearLayoutManager.HORIZONTAL, false)
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = layoutManager

        //val imageLoader: ImageLoader = ImageLoader.getInstance()
        //imageLoader.displayImage("drawable://" + R.drawable.sup_boston_louxi, dishPhoto)
        var getDish = MenuDB(this)
        var dish:DescriptionDish = getDish.getDescriptionDish(3)
        showDishDescription(getDish.getDescriptionDish(3))
        recyclerView.adapter = AdapterRecommendDish(listRecDish(dish))
    }

    fun showDishDescription(dish: DescriptionDish){
        //Проверка получены ли данные
        if (dish.price.toString() == "price" || dish.price == null){
            dishCena.text = "-"
        } else{
            dishCena.text = (dish.price).toString()
        }
        if (dish.weight == null || dish.weight == "weight"){
            dishVes.visibility = View.GONE
        } else{
            dishVes.text = dish.weight
        }
        if (dish.title == null || dish.title == "title"){
            dishName.text = "Без наименования"
        } else{
            dishName.text = dish.title
        }
        if (dish.descLong == "description_long" || dish.descLong == null){
            dishOpisanie.visibility = View.GONE
        } else{
            dishOpisanie.text = dish.descLong
        }

        val imageLoader: ImageLoader = ImageLoader.getInstance()
        imageLoader.displayImage(dish.photoUrl, dishPhoto, object: ImageLoadingListener {
            override fun onLoadingComplete(imageUri: String?, view: View?, loadedImage: Bitmap?) {
                progressBar.visibility = View.GONE
            }

            override fun onLoadingStarted(imageUri: String?, view: View?) {
                //imageLoader.displayImage("drawable://"+R.drawable.noimage,dishPhoto)
                progressBar.visibility = View.VISIBLE
            }

            override fun onLoadingCancelled(imageUri: String?, view: View?) {
                imageLoader.displayImage("drawable://"+R.drawable.noimage,dishPhoto)
            }

            override fun onLoadingFailed(imageUri: String?, view: View?, failReason: FailReason?) {
                imageLoader.displayImage("drawable://"+R.drawable.noimage,dishPhoto)
            }

        })
    }

    fun listRecDish(dish: DescriptionDish): ArrayList<DescriptionDish>{
        var str = dish.recommended
        var delimiter = ';'
        var parts = str.split(delimiter)
        var list: ArrayList<DescriptionDish> = ArrayList()
        var getDish = MenuDB(this)
        for (i in 0..parts.size-1){
            Log.d("Индекс", "$i")
            if (!parts.get(i).contains("non"))
                list.add(getDish.getDescriptionDish(parts.get(i).toInt()))
        }
        return list
    }

}
