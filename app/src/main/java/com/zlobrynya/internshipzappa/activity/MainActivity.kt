package com.zlobrynya.internshipzappa.activity

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.util.Log
import com.nostra13.universalimageloader.cache.disc.impl.LimitedAgeDiskCache
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator
import com.nostra13.universalimageloader.core.DisplayImageOptions
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration
import com.nostra13.universalimageloader.core.assist.ImageScaleType
import com.nostra13.universalimageloader.core.download.BaseImageDownloader
import com.zlobrynya.internshipzappa.R
import com.zlobrynya.internshipzappa.tools.GetDataServer
import com.zlobrynya.internshipzappa.tools.OurException
import com.zlobrynya.internshipzappa.tools.database.MenuDB
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    private lateinit var menuDb: MenuDB

    companion object {

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val options = DisplayImageOptions.Builder()
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
            .build()


        val config = ImageLoaderConfiguration.Builder(this)
            .threadPoolSize(5)
            .diskCache(LimitedAgeDiskCache(cacheDir, null, HashCodeFileNameGenerator(), (60 * 30).toLong()))
            .imageDownloader(BaseImageDownloader(this)) // connectTimeout (5 s), readTimeout (30 s)
            .defaultDisplayImageOptions(options)
            .build()
        ImageLoader.getInstance().init(config)
        ImageLoader.getInstance().displayImage("drawable://"+ R.drawable.launch_screan,launch)

        menuDb = MenuDB(this)

    }

    override fun onStart() {
        super.onStart()
        getData()
    }

    //качаем данные c сервера
    private fun getData(){
        val getDataServer = GetDataServer(this)
        getDataServer.getData()
            .subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe(object : Observer<Boolean> {
                override fun onComplete() {}

                override fun onSubscribe(d: Disposable) {}

                override fun onNext(t: Boolean) {
                    startMenu()
                }

                override fun onError(e: Throwable) {
                    val outE = e as OurException
                    Log.e("err", outE.codeRequest.toString())
                    when (outE.codeRequest){
                        0 -> {
                            if (menuDb.getCountRow() == 0){
                                allert(getString(R.string.code_0))
                            }else{
                                allert(getString(R.string.offline))
                            }
                        }
                        404, 500 -> {
                            if (menuDb.getCountRow() == 0){
                                allert(getString(R.string.code_404))
                            }else{
                                allert(getString(R.string.offline))
                            }
                        }
                        503 -> {
                            if (menuDb.getCountRow() == 0){
                                allert(getString(R.string.code_503))
                            }else{
                                allert(getString(R.string.offline))
                            }
                        }
                        else -> {
                            if (menuDb.getCountRow() == 0){
                                allert(getString(R.string.code_0))
                            }else{
                                allert(getString(R.string.offline))
                            }
                        }
                    }
                }
            })
    }

    override fun onDestroy() {
        menuDb.closeDataBase()
        super.onDestroy()
    }

    //вызов диалога
    private fun allert(text: String){
        val builder = AlertDialog.Builder(this)
            builder.setTitle(getString(R.string.something_wrong))
                .setMessage(text)
                .setCancelable(false)
                .setPositiveButton(getString(R.string.repeat_connection)
                ) { dialog, _ ->
                    run {
                        getData()
                        dialog.cancel()
                    }
                }
                .setNegativeButton(getString(R.string.well)
                ) { dialog, id ->
                    run {
                        startMenu()
                    }
                }
                .setNeutralButton(getString(R.string.call)){
                    dialog, id ->
                    run {
                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:+7(8142)63-23-89"))
                        if (intent.resolveActivity(packageManager) != null) {
                            startActivity(intent)
                        }
                    }
                }
            val alert = builder.create()
            alert.show()
    }

    fun startMenu(){
        val intent = Intent(this, MenuActivity::class.java)
        intent.setFlags(FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        finish()
    }
}
