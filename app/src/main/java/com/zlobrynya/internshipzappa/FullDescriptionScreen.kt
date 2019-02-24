package com.zlobrynya.internshipzappa

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.nostra13.universalimageloader.core.ImageLoader
import com.zlobrynya.internshipzappa.database.MenuDB
import com.zlobrynya.internshipzappa.tools.DescriptionDish
import kotlinx.android.synthetic.main.full_description_screen.*

class FullDescriptionScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.full_description_screen)

        //Создание RecyclerView
        val layoutManager = LinearLayoutManager(this@FullDescriptionScreen, LinearLayoutManager.HORIZONTAL, false)
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = layoutManager

        var getDish= MenuDB(this)
        showDishDescription(getDish.getDescriptionDish(2))
        recyclerView.adapter = AdapterRecommendDish(listRecDish(dish = DescriptionDish()))
    }


    fun showDishDescription(dish: DescriptionDish){
        dishCena.setText((dish.price).toString())
        val imageLoader: ImageLoader = ImageLoader.getInstance()
        imageLoader.displayImage(dish.photoUrl, dishPhoto)
    }

    fun listRecDish(dish: DescriptionDish): ArrayList<DescriptionDish>{
        var str = dish.recommended
        var delimiter = ';'
        var parts: ArrayList<DescriptionDish> = str.split(delimiter) as ArrayList<DescriptionDish>
        return parts
    }
}
