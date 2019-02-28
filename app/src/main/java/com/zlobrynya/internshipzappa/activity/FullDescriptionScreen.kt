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


class FullDescriptionScreen : AppCompatActivity() {

    private lateinit var menuDB: MenuDB


    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_description_screen)

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

        if (dish.weight == 0){
            dishVes.visibility = View.GONE
        } else{
            dishVes.text = dish.weight.toString() + getString(R.string.gr)
        }

        dishName.text = dish.name

        if (dish.desc_long.isEmpty()){
            dishOpisanie.visibility = View.GONE
        } else{
            dishOpisanie.text = dish.desc_long
        }

        val imageLoader: ImageLoader = ImageLoader.getInstance()
        imageLoader.displayImage(dish.photo, dishPhoto, object: ImageLoadingListener {
            override fun onLoadingComplete(imageUri: String?, view: View?, loadedImage: Bitmap?) {
                progressBar.visibility = View.GONE
            }

            override fun onLoadingStarted(imageUri: String?, view: View?) {
                progressBar.visibility = View.VISIBLE
            }

            override fun onLoadingCancelled(imageUri: String?, view: View?) {
                progressBar.visibility = View.GONE
                imageLoader.displayImage("drawable://"+ R.drawable.no_menu_duscr,dishPhoto)
            }

            override fun onLoadingFailed(imageUri: String?, view: View?, failReason: FailReason?) {
                progressBar.visibility = View.GONE
                imageLoader.displayImage("drawable://"+ R.drawable.no_menu_duscr,dishPhoto)
            }

        })
    }

    fun listRecDish(str: String): ArrayList<DishDTO>{
        val list: ArrayList<DishDTO> = ArrayList()
        if (!str.isEmpty()){
            val delimiter = ';'
            val parts = str.split(delimiter)
            for (i in 0..parts.size-1){
                Log.d("Индекс", "$i")
                if (!parts.get(i).contains("null"))
                    list.add(menuDB.getDescriptionDish(parts.get(i).toInt()))
            }
        }
        return list
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
}
