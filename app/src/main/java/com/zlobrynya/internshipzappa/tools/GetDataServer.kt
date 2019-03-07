package com.zlobrynya.internshipzappa.tools

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.zlobrynya.internshipzappa.R
import com.zlobrynya.internshipzappa.tools.database.CategoryDB
import com.zlobrynya.internshipzappa.tools.database.MenuDB
import com.zlobrynya.internshipzappa.tools.retrofit.RetrofitClientInstance
import com.zlobrynya.internshipzappa.tools.retrofit.dto.CatDTO
import com.zlobrynya.internshipzappa.tools.retrofit.dto.CatList
import com.zlobrynya.internshipzappa.tools.retrofit.dto.CheckDTO
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
                Log.i("check","it's fine1")
                RetrofitClientInstance.getInstance()
                    .getLogServer()
                    .subscribeOn(Schedulers.io())
                    ?.observeOn(AndroidSchedulers.mainThread())
                    ?.subscribe(object : Observer<Response<CheckDTO>> {
                        override fun onComplete() {}
                        override fun onSubscribe(d: Disposable) {
                            }
                        override fun onNext(t: Response<CheckDTO>) {
                            Log.i("check",t.body()!!.toString())
                            if (t.code() == 200) {
                                //
                                Log.i("check",t.body()!!.date)
                                checkPass(t.body()!!.date, emitter, t.body()!!.count)
                            } else {
                                //посылаем Error с кодом ошибки сервера
                                Log.e("err", t.code().toString())
                                closeBD()
                                emitter.onError(OurException(t.code()))
                            }
                        }

                        override fun onError(e: Throwable) {
                            Log.i("check","that's not fineIn")
                            println(e.toString())
                            closeBD()
                            emitter.onError(OurException())
                        }

                    })

            }
        })
    }

    //метод закрытие бд
    private fun closeBD(){
        menuDb.closeDataBase()
        categoryDB.closeDataBase()
    }


    private fun checkPass(log: String, emitter: ObservableEmitter<Boolean>, count: Int) {
        val sharedPreferences = context.getSharedPreferences(context.getString(R.string.key_shared_name), Context.MODE_PRIVATE)
        val savedLog = context.getString(R.string.key_shared_log)
        val savedText = sharedPreferences.getInt(savedLog, 0)

        Log.i("Log",log + " " + savedText)

        //если проходит проверку посылаем весь список
        if (log.hashCode() == savedText) {
            val countLocBD = menuDb.getCountRow()
            if (countLocBD == count){
                closeBD()
            }else{
                clearBD()
                getCategory(emitter)
            }
            /*RetrofitClientInstance.getInstance()
                .getLogServer()
                .subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(object : Observer<Response<CheckDTO>> {
                    override fun onComplete() {}
                    override fun onSubscribe(d: Disposable) {}

                    override fun onNext(t: Response<CheckDTO>) {
                        try {
                            if (t.code() == 200) {
                                val countSerBD = t.body()!!.count
                                val countLocBD = menuDb.getCountRow()
                                if (countLocBD == countSerBD){
                                    closeBD()
                                    emitter.onNext(true)
                                } else{
                                    clearBD()
                                    getCategory(emitter)
                                }
                            } else {
                                //посылаем Error с кодом ошибки сервера
                                emitter.onError(OurException(t.code()))
                            }
                        }catch (e: Throwable){
                            closeBD()
                            emitter.onError(OurException())
                        }
                    }

                    override fun onError(e: Throwable) {
                        println(e.toString())
                        emitter.onError(OurException())
                    }

                })*/
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
            .subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe(object : Observer<Response<CatList>> {
                override fun onComplete() {}

                override fun onSubscribe(d: Disposable) {}

                override fun onNext(t: Response<CatList>) {
                    if (t.code() == 200) {
                        val body = t.body()
                        val categories = body?.categories
                        categoryDB.addAllData(categories!!)
                        getCategoriesMenu(categories, emitter)
                    } else {
                        //посылаем Error с кодом ошибки сервера
                        closeBD()
                        emitter.onError(OurException(t.code()))
                    }

                }

                override fun onError(e: Throwable) {
                    println(e.toString())
                    closeBD()
                    emitter.onError(OurException())
                }
            })
    }

    @SuppressLint("CheckResult")
    private fun getCategoriesMenu(categories: List<CatDTO>, emitter: ObservableEmitter<Boolean>) {
        //Log.i("CategoriesMenu","getCategoriesMenu")
        val countCat = categories.size-1
        var countComplite = 0;
        val composite = CompositeDisposable()

        categories.forEach {
            val nameCategory = it.name
            composite.add(
                RetrofitClientInstance.getInstance()
                    .getAllDishes(it.class_id.toString())
                    .subscribeOn(Schedulers.io())
                    ?.observeOn(AndroidSchedulers.mainThread())
                    ?.subscribeWith(object : DisposableObserver<Response<DishList>>() {
                        override fun onComplete() {}
                        override fun onNext(t: Response<DishList>) {
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
                                }else if (countComplite == countCat){
                                    //посылаем сообщение что мы тут закончили
                                    closeBD()
                                    emitter.onNext(true)
                                }
                                else countComplite++;
                            } else {
                                //посылаем Error с кодом ошибки сервера
                                countComplite = -5
                                composite.clear()
                                closeBD()
                                emitter.onError(OurException(t.code()))
                            }
                        }

                        override fun onError(e: Throwable) {
                            println(e.toString())
                            countComplite = -5
                            composite.clear()
                            closeBD()
                            emitter.onError(e)
                        }})!!
           )
        }
    }
}