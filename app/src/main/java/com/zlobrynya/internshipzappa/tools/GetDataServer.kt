package com.zlobrynya.internshipzappa.tools

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.zlobrynya.internshipzappa.tools.database.CategoryDB
import com.zlobrynya.internshipzappa.tools.database.MenuDB
import com.zlobrynya.internshipzappa.tools.retrofit.RetrofitClientInstance
import com.zlobrynya.internshipzappa.tools.retrofit.dto.CatDTO
import com.zlobrynya.internshipzappa.tools.retrofit.dto.CatList
import com.zlobrynya.internshipzappa.tools.retrofit.dto.DishList
import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import retrofit2.Response
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver


/*
* Либо помещаем в сервис и он вызывается с определенной переодичностью
* */


class GetDataServer(val context: Context) {
    private var menuDb: MenuDB
    private var categoryDB: CategoryDB

    init {
        menuDb = MenuDB(context)
        categoryDB = CategoryDB(context)
    }

    fun getData(): Observable<Boolean> {
        return Observable.create(object : ObservableOnSubscribe<Boolean> {
            override fun subscribe(emitter: ObservableEmitter<Boolean>) {
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

    private fun checkPass(log: String, emitter: ObservableEmitter<Boolean>) {
        val sharedPreferences = context.getSharedPreferences("endpoint", Context.MODE_PRIVATE)
        val savedLog = "logK"
        val savedText = sharedPreferences.getInt(savedLog, 0)
        Log.i("checkPass", savedText.toString())
        Log.i("checkPass", log.hashCode().toString())

        //если проходит проверку посылаем весь список
        if (log.hashCode() == savedText) {
            RetrofitClientInstance.getInstance()
                .getDishCount()
                .subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(object : Observer<Response<ResponseBody>> {
                    override fun onComplete() {
                        println("Complete")
                    }

                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onNext(t: Response<ResponseBody>) {
                        try {
                            if (t.code() == 200) {
                                val countSerBD = t.body().toString().toInt()
                                val countLocBD = menuDb.getCountRow()
                                if (countLocBD == countSerBD)
                                    emitter.onNext(true)
                                else{
                                    clearBD()
                                    getCategory(emitter)
                                }
                            } else {
                                //посылаем Error с кодом ошибки сервера
                                emitter.onError(OurException(t.code()))
                            }
                        }catch (e: Throwable){
                            emitter.onError(OurException())
                        }
                    }

                    override fun onError(e: Throwable) {
                        println(e.toString())
                        emitter.onError(OurException())
                    }

                })
        } else {
            val editor = sharedPreferences.edit()
            editor.putInt(savedLog,log.hashCode())
            editor.apply()
            clearBD()
            //получаем категории
            getCategory(emitter)
        }
    }

    private fun clearBD(){
        //чистим таблицы
        menuDb.clearTableDB()
        categoryDB.clearTableDB()
    }

    private fun getCategory(emitter: ObservableEmitter<Boolean>) {
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
    private fun getCategoriesMenu(categories: List<CatDTO>, emitter: ObservableEmitter<Boolean>) {
        Log.i("CategoriesMenu","getCategoriesMenu")
        val countCat = categories.size-1
        var countComplite = 0;
        val composite = CompositeDisposable()

        categories.forEach {
            val url = "https://na-rogah-api.herokuapp.com/get_menu/" + it.class_id.toString()
            val nameCategory = it.name
            Log.i("CategoriesMenu","Start " + nameCategory)


           composite.add(
               RetrofitClientInstance.getInstance()
                   .getAllDishes(url)
                   ?.subscribeOn(Schedulers.io())
                   ?.observeOn(AndroidSchedulers.mainThread())
                   ?.subscribeWith(object : DisposableObserver<Response<DishList>>() {
                       override fun onComplete() {}
                       override fun onNext(t: Response<DishList>) {
                           Log.i("CategoriesMenu","c")
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

                               //считаем сколько потоков завершилось
                               if (countComplite < 0){
                                   composite.clear()
                               }else if (countComplite == countCat)
                                   //посылаем сообщение что мы тут закончили
                                   emitter.onNext(true)
                               else countComplite++;
                           } else {
                               //посылаем Error с кодом ошибки сервера
                               Log.e("CategoriesMenu","t.code()")
                               countComplite = -5
                               composite.clear()
                               emitter.onError(OurException(t.code()))
                           }
                       }

                       override fun onError(e: Throwable) {
                           println(e.toString())
                           countComplite = -5
                           composite.clear()
                           emitter.onError(e)
                       }})!!
           )
        }
    }
}