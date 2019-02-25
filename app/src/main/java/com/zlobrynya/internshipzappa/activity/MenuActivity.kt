package com.zlobrynya.internshipzappa.activity

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

        //выполняем проверку обновлений,
        LogCheck.getInstance().getLog().subscribeOn(Schedulers.newThread())
            ?.observeOn(AndroidSchedulers.mainThread())?.subscribe(object : Observer<String>{
                override fun onComplete() {
                }

                override fun onSubscribe(d: Disposable) {
                }

                override fun onNext(t: String) {
                    Log.i("CheckJS","onNext")
                    checkPass(t,this@MenuActivity)

                }

                override fun onError(e: Throwable) {
                    Log.i("CheckJS",e.message)
                }
            })

        //Get json data from file
        //В отдельном потоке подключаемся к серверу и какчаем json файл, парсим его
        //и получаем обьект MenuDish, в котом содержатся данные разбитые по категориям
        /*ParsJson.getInstance().getMenu(this).subscribeOn(Schedulers.newThread())
            ?.observeOn(AndroidSchedulers.mainThread())?.subscribe(object : Observer<MenuDish> {
                override fun onComplete() {
                    println("Complete")
                }

                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(menuDish: MenuDish) {
                    if (menuDish.connect){
                        viewPagerMenu.adapter = AdapterTab(supportFragmentManager, menuDish, 6)
                        sliding_tabs.setupWithViewPager(viewPagerMenu)
                        for (i in 0..5){
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                //sliding_tabs.getTabAt(i)?.icon = getDrawable(imageResId[i])
                                sliding_tabs.getTabAt(i)?.text = getString(stringId[i])
                            }
                        }
                    }else{
                        menuActivity!!.start()
                    }
                }
                override fun onError(e: Throwable) {
                    println(e.toString())
                }
            })*/
    }

    fun start(){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
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
            editor.putInt("logK", log.hashCode())
            editor.apply()

            //чистим таблицы
            menuDb.clearTableDB()
            categoryDB.clearTableDB()

            //обращение к серверу за списком блюд
            val service = RetrofitClientInstance.retrofitInstance?.create(GetCatService::class.java)
            val call = service?.getAllCategories()
            call?.enqueue(object : retrofit2.Callback<CatList> {
                override fun onResponse(call: Call<CatList>, response: Response<CatList>) {
                    val body = response.body()
                    val categories = body?.categories
                    //добавляем в бд категории
                    categoryDB.addAllData(categories!!)
                    getCategoriesMenu(categories)
                }
                override fun onFailure(call: Call<CatList>, t: Throwable) {
                    Toast.makeText(context, "error reading JSON categories", Toast.LENGTH_LONG).show()
                }
            })
        }
    }

    //устанавливаем в табы категориии
    private fun setCategories(categories: List<CatDTO>){
        //Log.i("categories", categories.size.toString())
        //Log.i("categories", "$categories")

        viewPagerMenu.adapter = AdapterTab(supportFragmentManager, categories, categories.size)
        sliding_tabs.setupWithViewPager(viewPagerMenu)
        for (i in 0..categories.size){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                //sliding_tabs.getTabAt(i)?.icon = getDrawable(imageResId[i])
                sliding_tabs.getTabAt(i)?.text = categories.get(i).name
            }
        }
    }

    //получаем с сервера категории меню
    private fun getCategoriesMenu(categories: List<CatDTO>){
        categories.forEach {
            val url = "https://na-rogah-api.herokuapp.com/get_menu/" + it.class_id.toString()
            val nameCategory = it.name
            //запрос к блюдам категории
            val service = RetrofitClientInstance.retrofitInstance?.create(GetDishService::class.java)
            val call = service?.getAllDishes(url)

            call?.enqueue(object : retrofit2.Callback<DishList>{
                override fun onFailure(call: Call<DishList>, t: Throwable) {
                    Toast.makeText(this@MenuActivity, "error reading JSON dishes", Toast.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call<DishList>, response: Response<DishList>) {
                    val body = response.body()
                    val dishes = body?.menu
                    dishes?.forEach {
                        //экранирование ковыче
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
                        //Log.i("replace", it.desc_long)
                    }
                    menuDb.addAllData(dishes!!)
                    setCategories(categories)
                    //Log.i("row2", menuDb.getCountRow().toString())
                }
            })
        }
    }


}

