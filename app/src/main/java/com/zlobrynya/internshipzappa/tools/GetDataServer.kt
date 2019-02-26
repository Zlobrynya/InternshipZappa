package com.zlobrynya.internshipzappa.tools

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.zlobrynya.internshipzappa.tools.database.CategoryDB
import com.zlobrynya.internshipzappa.tools.database.MenuDB
import com.zlobrynya.internshipzappa.tools.retrofit.GetRequest
import com.zlobrynya.internshipzappa.tools.retrofit.RetrofitClientInstance
import com.zlobrynya.internshipzappa.tools.retrofit.dto.CatDTO
import com.zlobrynya.internshipzappa.tools.retrofit.dto.CatList
import com.zlobrynya.internshipzappa.tools.retrofit.dto.DishList
import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import io.reactivex.Flowable




class GetDataServer(val context: Context) {
    private var menuDb: MenuDB
    private var categoryDB: CategoryDB

    init {
        menuDb = MenuDB(context)
        categoryDB = CategoryDB(context)
    }

    fun getData(): Observable<List<CatDTO>> {
        return Observable.create(object : ObservableOnSubscribe<List<CatDTO>> {
            override fun subscribe(emitter: ObservableEmitter<List<CatDTO>>) {
                RetrofitClientInstance.getInstance()
                    .getLogServer()
                    .subscribeOn(Schedulers.io())
                    ?.observeOn(AndroidSchedulers.mainThread())
                    ?.subscribe(object : Observer<Response<ResponseBody>> {
                        override fun onComplete() {
                            println("Complete")
                        }

                        override fun onSubscribe(d: Disposable) {

                        }

                        override fun onNext(t: Response<ResponseBody>) {
                            if (t.code() == 200) {
                                checkPass(t.body().toString(), emitter)
                            } else {
                                //посылаем Error с кодом ошибки сервера
                                emitter.onError(OurException(t.code()))
                            }
                        }

                        override fun onError(e: Throwable) {
                            println(e.toString())
                            val outException = OurException()
                            emitter.onError(OurException())
                        }

                    })

            }
        })
    }

    private fun checkPass(log: String, emitter: ObservableEmitter<List<CatDTO>>) {
        val sharedPreferences = context.getSharedPreferences("endpoint", Context.MODE_PRIVATE)
        val savedLog = "logK"
        val savedText = sharedPreferences.getInt(savedLog, 0)
        //если проходит проверку посылаем весь список
        if (log.hashCode() == savedText) {
            emitter.onNext(categoryDB.getCategory())
        } else {
            val editor = sharedPreferences.edit()
            editor.apply()

            //чистим таблицы
            menuDb.clearTableDB()
            categoryDB.clearTableDB()

            //получаем категории
            getCategory(emitter)
        }
    }

    private fun getCategory(emitter: ObservableEmitter<List<CatDTO>>) {
        RetrofitClientInstance.getInstance()
            .getAllCategories()
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe(object : Observer<Response<CatList>> {
                override fun onComplete() = println("Complete getAllCategories")

                override fun onSubscribe(d: Disposable) {}

                override fun onNext(t: Response<CatList>) {
                    if (t.code() == 200) {
                        val body = t.body()
                        val categories = body?.categories
                        categoryDB.addAllData(categories!!)
                        getCategoriesMenu(categories, emitter)
                    } else {
                        //посылаем Error с кодом ошибки сервера
                        emitter.onError(OurException(t.code()))
                    }

                }

                override fun onError(e: Throwable) {
                    println(e.toString())
                    emitter.onError(OurException())
                }
            })
    }

    @SuppressLint("CheckResult")
    private fun getCategoriesMenu(categories: List<CatDTO>, emitter: ObservableEmitter<List<CatDTO>>) {
        Log.i("CategoriesMenu","getCategoriesMenu")


        val f = Observable.create<Int> { t -> t.onNext(1) }.count()
        f.subscribe { x -> Log.e("fd",x.toString()) }


        categories.forEach {
            val url = "https://na-rogah-api.herokuapp.com/get_menu/" + it.class_id.toString()
            val nameCategory = it.name
            Log.i("Retrofit","Start " + nameCategory)

            RetrofitClientInstance.getInstance()
                .getAllDishes(url)
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(object : Observer<Response<DishList>> {
                    override fun onComplete() {}

                    override fun onSubscribe(d: Disposable) {}

                    override fun onNext(t: Response<DishList>) {
                        Log.i("getAllDishes DishList","c")
                        if (t.code() == 200) {
                            val body = t.body()
                            val dishes = body?.menu
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
                            Log.i("getAllDishes","compl")
                            //посылаем сообщение что мы тут закончили
                        } else {
                            //посылаем Error с кодом ошибки сервера
                            emitter.onError(OurException(t.code()))
                        }
                    }

                    override fun onError(e: Throwable) {
                        println(e.toString())
                        emitter.onError(e)
                    }
                })
        }
    }
}