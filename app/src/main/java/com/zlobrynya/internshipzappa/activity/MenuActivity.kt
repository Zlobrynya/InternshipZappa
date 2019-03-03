package com.zlobrynya.internshipzappa.activity

import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.zlobrynya.internshipzappa.R
import com.zlobrynya.internshipzappa.adapter.AdapterTab
import kotlinx.android.synthetic.main.activity_menu.*
import android.util.Log
import com.nostra13.universalimageloader.cache.disc.impl.LimitedAgeDiskCache
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator
import com.nostra13.universalimageloader.core.DisplayImageOptions
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration
import com.nostra13.universalimageloader.core.assist.ImageScaleType
import com.nostra13.universalimageloader.core.download.BaseImageDownloader
import com.zlobrynya.internshipzappa.tools.database.CategoryDB
import com.zlobrynya.internshipzappa.tools.retrofit.dto.CatDTO


class MenuActivity: AppCompatActivity() {
    private lateinit var categoryDB: CategoryDB



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)
        supportActionBar!!.setTitle(R.string.menu_toolbar)
        supportActionBar!!.elevation = 0.0F

        categoryDB = CategoryDB(this)
        setCategories(categoryDB.getCategory())
    }


    override fun onDestroy() {
        categoryDB.closeDataBase()
        super.onDestroy()
    }

    //устанавливаем в табы категориии
    private fun setCategories(categories: List<CatDTO>){
        Log.i("cat","$categories")
        viewPagerMenu.adapter = AdapterTab(supportFragmentManager, categories, categories.size)

        sliding_tabs.setupWithViewPager(viewPagerMenu)


        for (i in 0..categories.size){
            for (CatDTO in categories ){
                if (CatDTO.order == i + 1) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        sliding_tabs.getTabAt(i)?.text = CatDTO.name
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        if (!ImageLoader.getInstance().isInited){
            val options = DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
                .build()

            val config = ImageLoaderConfiguration.Builder(this)
                .threadPoolSize(3)
                .diskCache(LimitedAgeDiskCache(cacheDir, null, HashCodeFileNameGenerator(), (60 * 30).toLong()))
                .imageDownloader(BaseImageDownloader(this)) // connectTimeout (5 s), readTimeout (30 s)
                .defaultDisplayImageOptions(options)
                .build()
            ImageLoader.getInstance().init(config)
        }
    }
}

