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
    private lateinit var menuDb: MenuDB
    private lateinit var categoryDB: CategoryDB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        //!!!!!!!!!!!!временно здесь, потом передет в mainactivity
        val config = ImageLoaderConfiguration.Builder(this)
            .threadPoolSize(3)
            .denyCacheImageMultipleSizesInMemory()
            .memoryCache(UsingFreqLimitedMemoryCache(8 * 1024 * 1024)) // 2 Mb
            .diskCache(LimitedAgeDiskCache(cacheDir, null, HashCodeFileNameGenerator(), (60 * 60 * 30).toLong()))
            .imageDownloader(BaseImageDownloader(this)) // connectTimeout (5 s), readTimeout (30 s)
            .build()
        ImageLoader.getInstance().init(config)
        //!!!!!!!!!!!!

        menuDb = MenuDB(this)
        categoryDB = CategoryDB(this)
        getData()
    }

    //качаем данные c сервера
    private fun getData(){
        val getDataServer = GetDataServer(this)
        getDataServer.getData()
            .subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe(object : Observer<List<CatDTO>> {
                override fun onComplete() = println("Complete getAllCategories")

                override fun onSubscribe(d: Disposable) {}

                override fun onNext(t: List<CatDTO>) {
                    setCategories(t)
                }

                override fun onError(e: Throwable) {
                    val outE = e as OurException
                    Log.e("err", outE.codeRequest.toString())
                    when (outE.codeRequest){
                        0 -> allert(getString(R.string.code_0))
                        404, 500 -> allert(getString(R.string.code_404))
                        503 -> allert(getString(R.string.code_503))
                        else -> allert(getString(R.string.code_else))
                    }
                }
            })
    }

    //устанавливаем в табы категориии
    private fun setCategories(categories: List<CatDTO>){
        viewPagerMenu.adapter = AdapterTab(supportFragmentManager, categories, categories.size)
        sliding_tabs.setupWithViewPager(viewPagerMenu)
        for (i in 0..categories.size){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                sliding_tabs.getTabAt(i)?.text = categories.get(i).name
            }
        }
    }

    //вызов диалога
    private fun allert(text: String){
        val builder = AlertDialog.Builder(this)
        if (menuDb.getCountRow() ==  0){
            builder.setTitle("Упс! Что то не так!")
                .setMessage(text)
                .setCancelable(false)
                .setPositiveButton("Повторить соединение."
                ) { dialog, id ->
                    run {
                        getData()
                        dialog.cancel()
                    }
                }
                .setNegativeButton("Закрыть"
                ) { dialog, id ->
                    run {
                        dialog.cancel()
                    }
                }
            val alert = builder.create()
            alert.show()
        } else{
            setCategories(categoryDB.getCategory())
        }
    }

}

