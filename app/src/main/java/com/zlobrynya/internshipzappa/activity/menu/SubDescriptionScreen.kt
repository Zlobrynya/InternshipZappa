package com.zlobrynya.internshipzappa.activity.menu

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.zlobrynya.internshipzappa.R
import com.zlobrynya.internshipzappa.adapter.menu.AdapterSubMenuDescription
import com.zlobrynya.internshipzappa.tools.database.MenuDB
import com.zlobrynya.internshipzappa.tools.database.SubMenuDB
import com.zlobrynya.internshipzappa.tools.retrofit.DTOs.menuDTOs.DishClientDTO
import com.zlobrynya.internshipzappa.tools.retrofit.DTOs.menuDTOs.DishSubDTO
import kotlinx.android.synthetic.main.activity_full_description_screen.*
import kotlinx.android.synthetic.main.activity_sub_description_screen.*
import java.util.ArrayList

class SubDescriptionScreen : AppCompatActivity() {

    private lateinit var menuDB: MenuDB
    private lateinit var subMenuDB: SubMenuDB


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sub_description_screen)

        val intent = intent
        val id = intent.getIntExtra(getString(R.string.key_id),0)

        //ToolBar
       /* toolbar4.setBackgroundColor(resources.getColor(R.color.black_alpha_40))
        setSupportActionBar(toolbar4)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowTitleEnabled(false)*/

        subScrollView.smoothScrollTo(0,0)

        //Создание RecyclerView
        val layoutManager = LinearLayoutManager(this@SubDescriptionScreen, LinearLayoutManager.VERTICAL,false)
        subRecyclerView.layoutManager = layoutManager

        menuDB = MenuDB(this)
        subMenuDB = SubMenuDB(this)

        val dish = menuDB.getDescriptionDish(id)
        showDishDescription(dish)

        subMenuDB = SubMenuDB(this)
        val array = subMenuDB.getCategoryDish(dish.name)
        Log.i( "sub", array.size.toString())

        subRecyclerView.adapter = AdapterSubMenuDescription(array)
    }

    @SuppressLint("SetTextI18n")
    fun showDishDescription(dish: DishClientDTO){
        //Проверка получены ли данные

        subDishName.text = dish.name.replace("\'", "\"")

        //Glide
        Glide.with(this@SubDescriptionScreen)
            .asBitmap()
            .load(dish.photo) // Изображение для теста. Исходное значение dish.photo
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .placeholder(R.drawable.menu)
            .error(R.drawable.menu)
            .into(object: SimpleTarget<Bitmap>(){
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    subProgressBar.visibility = View.GONE
                    subDishPhoto.setImageBitmap(resource)
                }

                override fun onLoadFailed(errorDrawable: Drawable?) {
                    super.onLoadFailed(errorDrawable)
                    subProgressBar.visibility = View.GONE
                    subDishPhoto.setImageDrawable(errorDrawable)
                }
            })
    }

}
