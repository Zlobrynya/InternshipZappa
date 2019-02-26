package com.zlobrynya.internshipzappa

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration
import com.nostra13.universalimageloader.core.download.BaseImageDownloader
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator
import com.nostra13.universalimageloader.cache.disc.impl.LimitedAgeDiskCache
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache
import com.nostra13.universalimageloader.core.download.ImageDownloader
import com.zlobrynya.internshipzappa.database.MenuDB
import com.zlobrynya.internshipzappa.tools.DescriptionDish
import java.io.InputStream

import java.security.AccessController.getContext


class MainActivity : AppCompatActivity(){



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        configUIL()
        addBD()

        val intent = Intent(this, FullDescriptionScreen::class.java)
        startActivity(intent)
    }

    private fun configUIL(){
        //UIL config
        val config = ImageLoaderConfiguration.Builder(this)
            .threadPoolSize(3)
            .denyCacheImageMultipleSizesInMemory()
            .memoryCache(UsingFreqLimitedMemoryCache(8 * 1024 * 1024)) // 2 Mb
            .diskCache(LimitedAgeDiskCache(cacheDir, null, HashCodeFileNameGenerator(), (60 * 60 * 30).toLong()))
            .imageDownloader(BaseImageDownloader(this)) // connectTimeout (5 s), readTimeout (30 s)
            .build()
        ImageLoader.getInstance().init(config)
    }

    private fun addBD(){
        val menuDB = MenuDB(this)
        if (menuDB.getCountRow() == 0){
            val array = arrayListOf<DescriptionDish>()
            array.add(getDescriptionDish(0,250,"https://na-rogah-api.herokuapp.com/photos/504a517002.jpg","1;2;3"))
            array.add(getDescriptionDish(1,200,"https://na-rogah-api.herokuapp.com/photos/b02b7f8067.jpg","0;2;3"))
            array.add(getDescriptionDish(2,150,"https://na-rogah-api.herokuapp.com/photos/a4aa616024.jpg","0;1;3"))
            array.add(getDescriptionDish(3,2000,"https://na-rogah-api.herokuapp.com/photos/15b6ee2008.jpg","0;1;2"))
            menuDB.addAllData(array)
        }
        menuDB.closeDataBase()
    }

    private fun getDescriptionDish(i: Int, price: Int, url: String, rec: String):DescriptionDish{
        val descriptionDish = DescriptionDish()
        descriptionDish.dishId = i
        descriptionDish.price = price
        descriptionDish.photoUrl = url
        descriptionDish.weight = "100"
        descriptionDish.recommended = rec
        return descriptionDish
    }
}
