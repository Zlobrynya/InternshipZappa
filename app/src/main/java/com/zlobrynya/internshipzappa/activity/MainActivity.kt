package com.zlobrynya.internshipzappa.activity

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.View
import com.nostra13.universalimageloader.cache.disc.impl.LimitedAgeDiskCache
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration
import com.nostra13.universalimageloader.core.download.BaseImageDownloader
import com.zlobrynya.internshipzappa.R
import com.zlobrynya.internshipzappa.tools.GetDataServer
import com.zlobrynya.internshipzappa.tools.OurException
import com.zlobrynya.internshipzappa.tools.database.CategoryDB
import com.zlobrynya.internshipzappa.tools.database.MenuDB
import com.zlobrynya.internshipzappa.tools.retrofit.dto.CatDTO
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    private lateinit var menuDb: MenuDB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val config = ImageLoaderConfiguration.Builder(this)
            .threadPoolSize(3)
            .denyCacheImageMultipleSizesInMemory()
            .memoryCache(UsingFreqLimitedMemoryCache(8 * 1024 * 1024)) // 2 Mb
            .diskCache(LimitedAgeDiskCache(cacheDir, null, HashCodeFileNameGenerator(), (60 * 30).toLong()))
            .imageDownloader(BaseImageDownloader(this)) // connectTimeout (5 s), readTimeout (30 s)
            .build()
        ImageLoader.getInstance().init(config)

        ImageLoader.getInstance().displayImage("drawable://"+ R.drawable.launch_screan,launch)

        menuDb = MenuDB(this)
        getData()

    }

    //качаем данные c сервера
    private fun getData(){
        val getDataServer = GetDataServer(this)
        getDataServer.getData()
            .subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe(object : Observer<Boolean> {
                override fun onComplete() = println("Complete getAllCategories")

                override fun onSubscribe(d: Disposable) {}

                override fun onNext(t: Boolean) {
                    startMenu()
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
            startMenu()
        }
    }



    fun startMenu(){
        val intent = Intent(this, MenuActivity::class.java)
        intent.setFlags(FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        finish()
    }
}
