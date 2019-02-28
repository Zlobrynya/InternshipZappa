package com.zlobrynya.internshipzappa.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.zlobrynya.internshipzappa.R
import com.zlobrynya.internshipzappa.adapter.AdapterTab
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_menu.*
import android.util.Log
import com.zlobrynya.internshipzappa.tools.retrofit.*
import android.widget.Toast
import com.nostra13.universalimageloader.cache.disc.impl.LimitedAgeDiskCache
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration
import com.nostra13.universalimageloader.core.download.BaseImageDownloader
import com.zlobrynya.internshipzappa.tools.database.CategoryDB
import com.zlobrynya.internshipzappa.tools.database.MenuDB
import com.zlobrynya.internshipzappa.tools.retrofit.dto.CatDTO
import com.zlobrynya.internshipzappa.tools.retrofit.dto.CatList
import com.zlobrynya.internshipzappa.tools.retrofit.dto.DishList
import retrofit2.Call
import retrofit2.Response
import android.content.DialogInterface
import android.support.v7.app.AlertDialog
import com.zlobrynya.internshipzappa.tools.GetDataServer
import com.zlobrynya.internshipzappa.tools.OurException


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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                sliding_tabs.getTabAt(i)?.text = categories.get(i).name
            }
        }
    }

}

