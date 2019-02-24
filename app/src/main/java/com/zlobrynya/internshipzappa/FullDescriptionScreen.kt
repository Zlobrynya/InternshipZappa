package com.zlobrynya.internshipzappa

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.nostra13.universalimageloader.core.ImageLoader
import kotlinx.android.synthetic.main.full_description_screen.*
import kotlinx.android.synthetic.main.rect_recommend_dish.*

class FullDescriptionScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.full_description_screen)

        //Создание RecyclerView
        val layoutManager = LinearLayoutManager(this@FullDescriptionScreen, LinearLayoutManager.HORIZONTAL, false)
        val recyclerView: RecyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.setLayoutManager(layoutManager)
        recyclerView.adapter = AdapterRecommendDish(generateValues())

        //UIL

        val imageLoader: ImageLoader = ImageLoader.getInstance()
        var imageUrl = "drawable://" + R.drawable.noimage
        imageLoader.displayImage(imageUrl, dishPhoto)
        //imageLoader.displayImage(imageUrl, topingPhoto)
    }

    private fun generateValues(): List<String> {
        val values = ArrayList<String>()
        for (i in 0..2){
            values.add("Медово-горчичный соус, $i")
        }
        return values
    }

}
