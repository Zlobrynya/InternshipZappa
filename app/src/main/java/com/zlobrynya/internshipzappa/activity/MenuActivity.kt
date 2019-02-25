package com.zlobrynya.internshipzappa.activity

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.zlobrynya.internshipzappa.R
import com.zlobrynya.internshipzappa.adapter.AdapterTab
import com.zlobrynya.internshipzappa.tools.MenuDish
import com.zlobrynya.internshipzappa.tools.json.ParsJson
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_menu.*
import kotlinx.android.synthetic.main.activity_scrolling.*
import android.support.design.widget.AppBarLayout
import android.util.Log
import com.zlobrynya.internshipzappa.retrofit.*
import android.view.MenuItem
import android.widget.Toast
import com.zlobrynya.internshipzappa.database.MenuDB
import com.zlobrynya.internshipzappa.retrofit.dto.CatList
import com.zlobrynya.internshipzappa.retrofit.dto.DishList
import retrofit2.Call
import retrofit2.Response


class MenuActivity: AppCompatActivity() {

    private val stringId = intArrayOf(R.string.hot, R.string.salad, R.string.soup, R.string.burger, R.string.combo,
        R.string.beer, R.string.toping)

    //Я догадываюсь, что то это та еще херня, но я не знаю как это можно сделать по нормальному
    private var menuActivity: MenuActivity? = null
    var itemAuto: MenuItem? = null

    private lateinit var menuDb: MenuDB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scrolling)
        setSupportActionBar(toolbar)
        val title = getString(R.string.menu_toolbar)
        menuActivity = this

        menuDb = MenuDB(applicationContext)
        Log.i("row", menuDb.getCountRow().toString())
        Toast.makeText(applicationContext, menuDb.getDescriptionDish(26).desc_long, Toast.LENGTH_LONG).show()
        //выполняем проверку обновлений,
        LogCheck.getInstance().getLog().subscribeOn(Schedulers.newThread())
            ?.observeOn(AndroidSchedulers.mainThread())?.subscribe(object : Observer<String>{
                override fun onComplete() {
                }

                override fun onSubscribe(d: Disposable) {
                }

                override fun onNext(t: String) {
                    checkPass(t,applicationContext,menuDb)

                }

                override fun onError(e: Throwable) {
                }
            })

        //Get json data from file
        //В отдельном потоке подключаемся к серверу и какчаем json файл, парсим его
        //и получаем обьект MenuDish, в котом содержатся данные разбитые по категориям
        ParsJson.getInstance().getMenu(this).subscribeOn(Schedulers.newThread())
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
            })
    }

    fun start(){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        menuDb.closeDataBase()

    }

    //сверяем дату обновления, если нет совпадений - обновляем бд
    private fun checkPass(log: String, context: Context, menuDb: MenuDB){
        val sharedPreferences = context.getSharedPreferences("endpoint", Context.MODE_PRIVATE)
        val savedLog = "logK"
        val savedText = sharedPreferences.getInt(savedLog ,0)
        if (log?.hashCode() == savedText){

        } else{
            val editor = sharedPreferences.edit()
            editor.putInt("logK", log.hashCode())
            editor.apply()

            menuDb.clearTableDB()
            //Пока что сюда запихну обращение к серверу за списком блюд
            val service = RetrofitClientInstance.retrofitInstance?.create(GetCatService::class.java)
            val call = service?.getAllCategories()

            call?.enqueue(object : retrofit2.Callback<CatList> {

                override fun onResponse(call: Call<CatList>, response: Response<CatList>) {

                    val body = response?.body()
                    val categories = body?.categories

                    categories?.forEach {
                        val url = "https://na-rogah-api.herokuapp.com/get_menu/" + it.class_id.toString()

                        //запрос к блюдам категории
                        val service = RetrofitClientInstance.retrofitInstance?.create(GetDishService::class.java)
                        val call = service?.getAllDishes(url)
                        call?.enqueue(object : retrofit2.Callback<DishList>{
                            override fun onFailure(call: Call<DishList>, t: Throwable) {

                                Toast.makeText(context, "error reading JSON dishes", Toast.LENGTH_LONG).show()

                            }
                            override fun onResponse(call: Call<DishList>, response: Response<DishList>) {

                                val body = response?.body()
                                val dishes = body?.menu
                                dishes?.forEach {
                                    //экранирование ковычек
                                    it.desc_long = it.desc_short.replace('\"', '\'')
                                    it.desc_long = it.desc_long.replace('\"', '\'')
                                    it.desc_long = it.name.replace('\"', '\'')
                                    it.desc_long = it.photo.replace('\"', '\'')
                                    it.desc_long = it.recommended.replace('\"', '\'')
                                    Log.i("replace", it.desc_long)}

                                menuDb.addAllData(dishes!!)
                                Log.i("row2", menuDb.getCountRow().toString())

                            }
                        })
                    }
                }
                override fun onFailure(call: Call<CatList>, t: Throwable) {
                    Toast.makeText(context, "error reading JSON categories", Toast.LENGTH_LONG).show()
                }
            })
        }
    }
}

