package com.zappa.narogah.activity.menu

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.MenuItem
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.zappa.narogah.R
import com.zappa.narogah.adapter.menu.AdapterRecommendDish
import com.zappa.narogah.adapter.menu.AdapterSubMenuDescription
import com.zappa.narogah.tools.database.MenuDB
import com.zappa.narogah.tools.database.SubMenuDB
import com.zappa.narogah.tools.retrofit.DTOs.menuDTOs.DishClientDTO
import kotlinx.android.synthetic.main.activity_sub_description_screen.*
import java.util.*

class SubDescriptionScreen : AppCompatActivity() {

    private lateinit var menuDB: MenuDB
    private lateinit var subMenuDB: SubMenuDB


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sub_description_screen)

        val intent = intent
        val id = intent.getIntExtra(getString(R.string.key_id),0)

        //ToolBar
        toolbar4.setBackgroundColor(resources.getColor(R.color.black_alpha_40))
        setSupportActionBar(toolbar4)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowTitleEnabled(false)

        subScrollView.smoothScrollTo(0,0)

        //Создание RecyclerView
        val layoutManager = LinearLayoutManager(this@SubDescriptionScreen, LinearLayoutManager.VERTICAL,false)
        subRecyclerView.layoutManager = layoutManager

        //Создание RecyclerView
        val layoutManagerRec = LinearLayoutManager(this@SubDescriptionScreen, LinearLayoutManager.HORIZONTAL, false)
        recyclerViewRec.layoutManager = layoutManagerRec

        menuDB = MenuDB(this)
        subMenuDB = SubMenuDB(this)

        val dish = menuDB.getDescriptionDish(id)
        showDishDescription(dish)

        subMenuDB = SubMenuDB(this)
        val array = subMenuDB.getCategoryDish(dish.name)
        Log.i( "sub", array.size.toString())

        subRecyclerView.adapter = AdapterSubMenuDescription(array)
        if (!array.isEmpty())
            recyclerViewRec.adapter =
                AdapterRecommendDish(listRecDish(dish.recommended))

        subMenuDB.closeDataBase()
        menuDB.closeDataBase()
    }

    @SuppressLint("SetTextI18n")
    fun showDishDescription(dish: DishClientDTO){
        //Проверка получены ли данные

        subDishName.text = dish.name.replace("\'", "\"")

        //Glide
        Glide.with(this@SubDescriptionScreen)
            .asBitmap()
            .load(dish.photo) // Изображение для теста. Исходное значение dish.photo
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            //.skipMemoryCache(true)
            .placeholder(R.drawable.menu)
            .centerCrop()
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            android.R.id.home -> {
                this.finish()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    fun listRecDish(str: String): ArrayList<DishClientDTO> {
        val list: ArrayList<DishClientDTO> = ArrayList()
        if (!str.isEmpty()){
            val delimiter = ';'
            val parts = str.split(delimiter)
            for (i in 0 until parts.size){
                if (!parts.get(i).contains("null"))
                    list.add(menuDB.getDescriptionDish(parts.get(i).toInt()))
            }
            if (list.size == 0)
                textView8.visibility = View.GONE
        }else{
            textView8.visibility = View.GONE
        }
        return list
    }

    override fun onDestroy() {
        menuDB.closeDataBase()
        super.onDestroy()
    }

}
