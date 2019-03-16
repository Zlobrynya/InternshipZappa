package com.zlobrynya.internshipzappa.activity.menu

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.zlobrynya.internshipzappa.adapter.menu.AdapterRecommendDish
import com.zlobrynya.internshipzappa.R
import com.zlobrynya.internshipzappa.tools.database.MenuDB
import com.zlobrynya.internshipzappa.tools.retrofit.DTOs.menuDTOs.DishDTO
import kotlinx.android.synthetic.main.activity_full_description_screen.*
import android.view.MenuItem
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import java.util.*


class FullDescriptionScreen : AppCompatActivity() {

    private lateinit var menuDB: MenuDB



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
            recyclerView.adapter =
                AdapterRecommendDish(listRecDish(dish.recommended))
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
            .load(dish.photo) // Изображение для теста. Исходное значение dish.photo
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .placeholder(R.drawable.menu)
            .error(R.drawable.menu)
            .into(object: SimpleTarget<Bitmap>(){
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    progressBar.visibility = View.GONE
                    dishPhoto.setImageBitmap(resource)
                }

                override fun onLoadFailed(errorDrawable: Drawable?) {
                    super.onLoadFailed(errorDrawable)
                    progressBar.visibility = View.GONE
                    dishPhoto.setImageDrawable(errorDrawable)
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
        } else{
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
    }
}

