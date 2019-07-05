package com.zlobrynya.internshipzappa.tools

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.zlobrynya.internshipzappa.R
import com.zlobrynya.internshipzappa.tools.database.CategoryDB
import com.zlobrynya.internshipzappa.tools.database.MenuDB
import com.zlobrynya.internshipzappa.tools.database.SubMenuDB
import com.zlobrynya.internshipzappa.tools.database.VisitingHoursDB
import com.zlobrynya.internshipzappa.tools.retrofit.DTOs.CheckDTO
import com.zlobrynya.internshipzappa.tools.retrofit.DTOs.bookingDTOs.visitingHoursList
import com.zlobrynya.internshipzappa.tools.retrofit.DTOs.menuDTOs.CatDTO
import com.zlobrynya.internshipzappa.tools.retrofit.DTOs.menuDTOs.CatList
import com.zlobrynya.internshipzappa.tools.retrofit.DTOs.menuDTOs.DishList
import com.zlobrynya.internshipzappa.tools.retrofit.RetrofitClientInstance
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import retrofit2.Response


/*
* Либо помещаем в сервис и он вызывается с определенной переодичностью
* */


class GetDataServer(val context: Context) {
    private var menuDb: MenuDB
    private var categoryDB: CategoryDB
    private var hoursDB: VisitingHoursDB
    private var subMenuDB: SubMenuDB
    init {
        hoursDB = VisitingHoursDB(context)
        menuDb = MenuDB(context)
        categoryDB = CategoryDB(context)
        subMenuDB = SubMenuDB(context)
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
                                checkPass(t.body()!!.date, emitter, t.body()!!.count, t.body()!!.time)

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
        subMenuDB.closeDataBase()
    }


    private fun checkPass(log: String, emitter: ObservableEmitter<Boolean>, count: Int, timeLog: String) {
        //проверка меню
        val sharedPreferences = context.getSharedPreferences(context.getString(R.string.key_shared_name), Context.MODE_PRIVATE)
        val savedLog = context.getString(R.string.key_shared_log)
        val savedText = sharedPreferences.getInt(savedLog, 0)
        //проверка расписания
        val sharedPreferencesTime = context.getSharedPreferences(context.getString(R.string.key_shared_time), Context.MODE_PRIVATE)
        val savedTime = context.getString(R.string.key_time)
        val savedTimeIn = sharedPreferencesTime.getInt(savedTime, 0)
        //создание префов для статуса
        val sharedPreferencesStat = context.getSharedPreferences(context.getString(R.string.user_info), Context.MODE_PRIVATE)
        val savedEmail = context.getString(R.string.user_email)
        val uuid = context.getString(R.string.access_token)
        if(sharedPreferencesStat.getString(savedEmail, "null") == "null"){
            val editor = sharedPreferencesStat.edit()
            editor.putString(savedEmail, "")
            editor.putString(uuid, "")
            editor.apply()
        }

        if(timeLog.hashCode() == savedTimeIn) {
            hoursDB.closeDataBase()
        }else{
            val editor = sharedPreferences.edit()
            editor.putInt(savedLog,timeLog.hashCode())
            editor.apply()
            hoursDB.clearTableDB()
            getTimeTable(emitter)
        }

        //если проходит проверку посылаем весь список
        Log.i("LogCHECK", log.hashCode().toString() + " " + savedText)
        if (log.hashCode() == savedText) {
            val countLocBD = menuDb.getCountRow()
            Log.i("LogCHECK", countLocBD.toString() + " " + count.toString())
            if (countLocBD == count){
                closeBD()
                emitter.onNext(true)
            }else{
                Log.i("Log","db is updating")
                clearBD()
                getCategory(emitter)
            }
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
        subMenuDB.clearTableDB()
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
                        Log.i("countof", categories!!.size.toString())
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

    private fun getTimeTable(emitter: ObservableEmitter<Boolean>) {
        RetrofitClientInstance.getInstance()
            .getTimeTable()
            .subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe(object : Observer<Response<visitingHoursList>> {
                override fun onComplete() {}

                override fun onSubscribe(d: Disposable) {}

                override fun onNext(t: Response<visitingHoursList>) {
                    if (t.code() == 200) {
                        val body = t.body()
                        val hours = body?.data
                        hoursDB.addAllData(hours!!)
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
        var countComplite = 0
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
                                    Log.i("dish", it.toString())
                                    try {
                                        if (it.class_name == null){it.class_name = ""}
                                        if (it.desc_long == null){it.desc_long = ""}
                                        if (it.desc_short == null){it.desc_short = ""}
                                        if (it.recommended == null){it.recommended = ""}
                                        if (it.photo == null){it.photo = ""}
                                        if (it.delivery == null){it.delivery = ""}
                                        if (it.weight == null){it.delivery = ""}

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

                                dishes?.forEach {
                                    if(!it.sub_menu!!.isEmpty()){
                                        it.sub_menu!!.forEach {
                                            /*if (it.name == null){it.name = ""}
                                            if (it.parent_name == null){it.parent_name = ""}
                                            if (it.weight == null){it.weight = ""}*/

                                            it.name = it.name.replace('\"', '\'')
                                            it.parent_name = it.parent_name.replace('\"', '\'')
                                            it.weight = it.weight.replace('\"', '\'')
                                            Log.i("dish", it.toString())
                                        }
                                        subMenuDB.addAllData(it.sub_menu!!)
                                    }
                                }
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