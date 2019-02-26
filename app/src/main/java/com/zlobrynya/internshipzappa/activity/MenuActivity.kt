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
        //качаем данные
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
                    Log.e("err","------------------------")
                    e.printStackTrace()
                    Log.e("err","------------------------")
                }
            })
    }

    //устанавливаем в табы категориии
    private fun setCategories(categories: List<CatDTO>){
        viewPagerMenu.adapter = AdapterTab(supportFragmentManager, categories, categories.size)
        sliding_tabs.setupWithViewPager(viewPagerMenu)
        for (i in 0..categories.size){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                //sliding_tabs.getTabAt(i)?.icon = getDrawable(imageResId[i])
                sliding_tabs.getTabAt(i)?.text = categories.get(i).name
            }
        }
    }


 /*   private fun logCheck(){
        //выполняем проверку обновлений,
        LogCheck.getInstance().getLog().subscribeOn(Schedulers.newThread())
            ?.observeOn(AndroidSchedulers.mainThread())?.subscribe(object : Observer<LogClass>{
                override fun onComplete() {
                }

                override fun onSubscribe(d: Disposable) {
                }

                override fun onNext(t: LogClass) {
                    Log.i("CheckJS",t.str)
                    if (t.code == 200){
                        checkPass(t.str,this@MenuActivity)
                    }else{
                        checkResponseCode(t.code)
                    }
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                }

                //проверка кода ответа
                private fun checkResponseCode(code: Int){
                    if (code == 0)
                        allert("Проверьте интернет соединение.")
                    else if (code >= 500 && code <= 599)
                        allert("Проблемы на сервере")
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
                .setPositiveButton("Повторить соединение.",
                    { dialog, id ->
                        run {
                            logCheck()
                            dialog.cancel()
                        }
                    })
                .setNegativeButton("Закрыть",
                    { dialog, id ->
                        run {
                            dialog.cancel()
                        }
                    })
            val alert = builder.create()
            alert.show()
        } else{
            setCategories(categoryDB.getCategory())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        menuDb.closeDataBase()
        categoryDB.closeDataBase()
    }

    //сверяем дату обновления, если нет совпадений - обновляем бд
    private fun checkPass(log: String, context: Context){
        val sharedPreferences = context.getSharedPreferences("endpoint", Context.MODE_PRIVATE)
        val savedLog = "logK"
        val savedText = sharedPreferences.getInt(savedLog ,0)
        if (log.hashCode() == savedText){
            setCategories(categoryDB.getCategory())
        } else{
            val editor = sharedPreferences.edit()
            editor.apply()

            //чистим таблицы
            menuDb.clearTableDB()
            categoryDB.clearTableDB()

            //обращение к серверу за списком блюд
            val service = RetrofitClientInstance.retrofitInstance?.create(GetRequest::class.java)
            val call = service?.getAllCategories()
            call?.enqueue(object : retrofit2.Callback<CatList> {
                override fun onResponse(call: Call<CatList>, response: Response<CatList>) {
                    val body = response.body()
                    val categories = body?.categories
                    Log.i("Response", response.message())
                    categoryDB.addAllData(categories!!)
                    getCategoriesMenu(categories)
                }
                override fun onFailure(call: Call<CatList>, t: Throwable) {
                    Toast.makeText(context, "error reading JSON categories", Toast.LENGTH_LONG).show()
                }
            })
        }
    }



    //получаем с сервера категории меню
    private fun getCategoriesMenu(categories: List<CatDTO>){
        Log.i("Retrofit","Start")
        categories.forEach {
            val url = "https://na-rogah-api.herokuapp.com/get_menu/" + it.class_id.toString()
            val nameCategory = it.name
            Log.i("Retrofit","Start " + nameCategory)

            //запрос к блюдам категории
            val service = RetrofitClientInstance.retrofitInstance?.create(GetRequest::class.java)
            val call = service?.getAllDishes(url)

            call?.enqueue(object : retrofit2.Callback<DishList>{
                override fun onFailure(call: Call<DishList>, t: Throwable) {
                    Toast.makeText(this@MenuActivity, "error reading JSON dishes", Toast.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call<DishList>, response: Response<DishList>) {
                    val body = response.body()
                    val dishes = body?.menu

                    Log.i("Retrofit","start")

                    dishes?.forEach {
                        //экранирование ковычек
                        try {
                            it.desc_short = it.desc_short.replace('\"', '\'')
                            it.desc_long = it.desc_long.replace('\"', '\'')
                            it.name = it.name.replace('\"', '\'')
                            it.photo = it.photo.replace('\"', '\'')
                            it.recommended = it.recommended.replace('\"', '\'')
                        }catch (e: IllegalArgumentException){
                            Log.i("error",e.message)
                        }
                        it.class_name = nameCategory
                    }
                    menuDb.addAllData(dishes!!)
                    Log.i("Retrofit","End Download" + nameCategory)
                    //контрольный запрос на сервер для уточнение количетсво записей в бд и на сервере, что б в случае чего презагрузить бд
                    //
                    setCategories(categories)
                }
            })

            Log.i("Retrofit","End " + nameCategory)
        }

    }*/


}

