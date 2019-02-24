package com.zlobrynya.internshipzappa.activity

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

        //Toast.makeText(applicationContext, menuDb.getCountRow().toString(), Toast.LENGTH_LONG).show()
        //Пока что сюда запихну обращение к серверу за списком блюд
        val service = RetrofitClientInstance.retrofitInstance?.create(GetCatService::class.java)
        val call = service?.getAllCategories()

        call?.enqueue(object : retrofit2.Callback<CatList> {

            override fun onResponse(call: Call<CatList>, response: Response<CatList>) {

                val body = response?.body()
                val categories = body?.categories
                //Toast.makeText(applicationContext, "that's fine", Toast.LENGTH_LONG).show()
                categories?.forEach {
                    val url = "https://na-rogah-api.herokuapp.com/get_menu/" + it.class_id.toString()
                    //Toast.makeText(applicationContext, it.class_id.toString(), Toast.LENGTH_LONG).show()
                    //запрос к блюдам категории
                    val service = RetrofitClientInstance.retrofitInstance?.create(GetDishService::class.java)
                    val call = service?.getAllDishes(url)
                    call?.enqueue(object : retrofit2.Callback<DishList>{
                        override fun onFailure(call: Call<DishList>, t: Throwable) {

                            Toast.makeText(applicationContext, "error reading JSON dishes", Toast.LENGTH_LONG).show()

                        }

                        override fun onResponse(call: Call<DishList>, response: Response<DishList>) {
                            val body = response?.body()
                            val dishes = body?.menu
                            //catSize.text = dishes?.get(0)?.name.toString()
                            //dishes?.forEach { Toast.makeText(applicationContext, it.item_id.toString(), Toast.LENGTH_LONG).show() }
                            //Toast.makeText(applicationContext, "u got the data", Toast.LENGTH_LONG).show()
                            menuDb.addAllData(dishes!!)
                            //запись в бд
                            dishes.forEach {
                                Toast.makeText(applicationContext, it.toString(), Toast.LENGTH_LONG).show()
                            }
                        }
                    })
                }

                //Toast.makeText(applicationContext, menuDb.getCountRow().toString(), Toast.LENGTH_LONG).show()

            }
            override fun onFailure(call: Call<CatList>, t: Throwable) {
                Toast.makeText(applicationContext, "error reading JSON categories", Toast.LENGTH_LONG).show()

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
}