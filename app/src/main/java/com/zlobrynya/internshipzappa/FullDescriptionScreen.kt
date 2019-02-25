package com.zlobrynya.internshipzappa

import android.content.Intent
import android.graphics.Bitmap
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.assist.FailReason
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener
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

        var getDish = MenuDB(this)
        var dish:DescriptionDish = getDish.getDescriptionDish(0)
        showDishDescription(getDish.getDescriptionDish(0))
        recyclerView.adapter = AdapterRecommendDish(listRecDish(dish))
    }


    fun showDishDescription(dish: DescriptionDish){
        dishCena.text = (dish.price).toString()
        dishName.text = (dish.title).toString()
        dishVes.text = (dish.weight).toString()
        val imageLoader: ImageLoader = ImageLoader.getInstance()
        imageLoader.displayImage(dish.photoUrl, dishPhoto, object: ImageLoadingListener {
            override fun onLoadingComplete(imageUri: String?, view: View?, loadedImage: Bitmap?) {
                progressBar.visibility = View.GONE
            }

            override fun onLoadingStarted(imageUri: String?, view: View?) {
                progressBar.visibility = View.VISIBLE
            }

            override fun onLoadingCancelled(imageUri: String?, view: View?) {
            }

            override fun onLoadingFailed(imageUri: String?, view: View?, failReason: FailReason?) {
            }

        })
    }

    fun listRecDish(dish: DescriptionDish): ArrayList<DescriptionDish>{
        var str = dish.recommended
        var delimiter = ';'
        var parts = str.split(delimiter)
        var list: ArrayList<DescriptionDish> = ArrayList()
        var getDish = MenuDB(this)
        for (i in 0..parts.size){
            if (parts.contains("$i")) {
                list.add(getDish.getDescriptionDish(parts.get(i).toInt()))
            }
        }
        return list
    }

    fun otherDishDisc(){
        val intent: Intent = Intent(this, FullDescriptionScreen::class.java)
        //место для обработчика кнопки для перехода на рекомендованное блюдо
    }
}
