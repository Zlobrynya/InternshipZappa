package com.zlobrynya.internshipzappa

import android.graphics.Bitmap
import android.nfc.Tag
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.nostra13.universalimageloader.core.DisplayImageOptions
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.assist.FailReason
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener
import kotlinx.android.synthetic.main.full_description_screen.*
import kotlinx.android.synthetic.main.rect_recommend_dish.*

class FullDescriptionScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.full_description_screen)

        //Создание RecyclerView
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.setLayoutManager(layoutManager)
        recyclerView.adapter = AdapterRecommendDish(generateValues())

        //UIL
        val imageLoader: ImageLoader = ImageLoader.getInstance()
        var imageUrl = "drawable://" + R.drawable.noimage
        imageLoader.displayImage(imageUrl, dishPhoto)
        imageLoader.displayImage(imageUrl, topingPhoto)
    }

    private fun generateValues(): List<String> {
        val values = mutableListOf<String>()
        for (i in 0..3){
            values.add("Медово-горчичный соус, $i")
        }
        return values
    }

}
