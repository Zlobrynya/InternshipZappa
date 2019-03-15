package com.zlobrynya.internshipzappa.activity.booking

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.zlobrynya.internshipzappa.R
import com.zlobrynya.internshipzappa.adapter.booking.AdapterTable
import kotlinx.android.synthetic.main.activity_table_select.*
import android.support.v7.widget.DividerItemDecoration
import android.util.Log
import com.zlobrynya.internshipzappa.adapter.booking.Table
import com.zlobrynya.internshipzappa.tools.OurException
import com.zlobrynya.internshipzappa.tools.retrofit.DTOs.bookingDTOs.bookingDataDTO
import com.zlobrynya.internshipzappa.tools.retrofit.DTOs.bookingDTOs.tableDTO
import com.zlobrynya.internshipzappa.tools.retrofit.DTOs.bookingDTOs.tableList
import com.zlobrynya.internshipzappa.tools.retrofit.PostRequest
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
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import kotlin.collections.ArrayList


/**
 * Активити для выбора столика для бронирования
 */
class TableSelectActivity : AppCompatActivity(), AdapterTable.OnTableListener {


    /**
     * Объект для POST запроса
     */
    private val newBooking = bookingDataDTO()

    /**
     * Обработчик нажатий на стрелочку в тулбаре
     */
    private val navigationClickListener = View.OnClickListener {
        this.finish()
    }

    /**
     * Ответ сервера
     */
    var responseBody: tableList? = null

    /**
     * Список столиков
     */
    private val tableList: ArrayList<Table> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_table_select)

        initRequest()
        initToolBar()
        networkRxjavaPost()

        //networkPost()
        //initTableList()
        //initRecycler()
    }

    /**
     * Заполняет объект для POST запрос
     */
    private fun initRequest() {
        val bookDateBegin = intent.getStringExtra("book_date_begin")
        //поменять на реальную дату окончания
        val bookDateEnd = intent.getStringExtra("book_date_end")
        val bookTimeBegin = intent.getStringExtra("book_time_begin")
        val bookTimeEnd = intent.getStringExtra("book_time_end")
        newBooking.date = bookDateBegin
        newBooking.time_from = bookTimeBegin
        newBooking.time_to = bookTimeEnd
        newBooking.date_to = bookDateEnd
        Log.d("TOPKEK", bookDateBegin)
        Log.d("TOPKEK", bookTimeBegin)
        Log.d("TOPKEK", bookTimeEnd)
    }

    /**
     * Отправляет POST запрос на сервер и получает в ответе список доступных столиков
     */
    private fun networkPost() {
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        val client = OkHttpClient.Builder()
            .addInterceptor(interceptor)
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://na-rogah-api.herokuapp.com/api/v1/")
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .client(client.build())
            .build()
        val apiInterface: PostRequest = retrofit.create(PostRequest::class.java)
        val requestCall = apiInterface.postBookingData(newBooking)
        /*requestCall.enqueue(object : Callback<tableList> {
            override fun onFailure(call: Call<tableList>, t: Throwable) {}

            override fun onResponse(call: Call<tableList>, response: Response<tableList>) {
                if (response.isSuccessful) {
                    Log.i("check1", "${response.code()}")
                    responseBody = response.body()
                    if (responseBody != null) {
                        if (responseBody!!.data.isEmpty()) { // Если свободных столиков нету, то выведем сообщение об этом
                            table_recycler.visibility = View.GONE
                            no_tables_available.visibility = View.VISIBLE
                        } else {
                            Log.d("TOPKEK", responseBody!!.data.size.toString())
                            initTableList()
                            initRecycler()
                        }
                    } else { // Если свободных столиков нету, то выведем сообщение об этом
                        table_recycler.visibility = View.GONE
                        no_tables_available.visibility = View.VISIBLE
                    }
                } else {
                    Log.i("check2", "${response.code()}")
                }
            }
        })*/
    }

    private fun networkRxjavaPost(): Observable<Boolean>{
        return Observable.create(object : ObservableOnSubscribe<Boolean>{
            override fun subscribe(emitter: ObservableEmitter<Boolean>) {
                Log.d("rxjava", "зашёл")
                RetrofitClientInstance.getInstance()
                    .postBookingDate(newBooking)
                    .subscribeOn(Schedulers.io())
                    ?.observeOn(AndroidSchedulers.mainThread())
                    ?.subscribe(object : Observer<Response<tableList>> {

                        override fun onComplete() {}

                        override fun onSubscribe(d: Disposable) {}

                        override fun onNext(t: Response<tableList>) {
                            if (t.isSuccessful) {
                                responseBody = t.body()
                                Log.i("check1", "${t.code()}")
                                if (t.body() != null) {
                                    if (t.body()!!.data.isEmpty()) { // Если свободных столиков нету, то выведем сообщение об этом
                                        table_recycler.visibility = View.GONE
                                        no_tables_available.visibility = View.VISIBLE
                                    } else {
                                        Log.d("TOPKEK", t.body()!!.data.size.toString())
                                        initTableList()
                                        initRecycler()
                                    }
                                } else { // Если свободных столиков нету, то выведем сообщение об этом
                                    table_recycler.visibility = View.GONE
                                    no_tables_available.visibility = View.VISIBLE
                                }
                            } else {
                                Log.i("check2", "${t.code()}")
                            }
                        }

                        override fun onError(e: Throwable) {
                            Log.i("check","that's not fineIn")
                            println(e.toString())
                            emitter.onError(OurException())
                        }

                    })
            }

        })
    }

    private fun post(tables: List<tableDTO>, emitter: ObservableEmitter<Boolean>){
        val countCat = tables.size-1
        var countComplite = 0
        val composite = CompositeDisposable()

        tables.forEach {
            Log.d("post", "зашёл")
            val tableId = it.table_id
            composite.add(
                RetrofitClientInstance.getInstance()
                    .postBookingDate(newBooking)
                    .subscribeOn(Schedulers.io())
                    ?.observeOn(AndroidSchedulers.mainThread())
                    ?.subscribeWith(object: DisposableObserver<Response<tableList>>(){
                        override fun onComplete() {}

                        override fun onNext(t: Response<tableList>) {
                            if (t.code() == 200) {
                                Log.d("onNext", "зашёл")
                                responseBody = t.body()
                                val tables = responseBody?.data
                                tables?.forEach {
                                    if (t.body() != null) {
                                        if (t.body()!!.data.isEmpty()) { // Если свободных столиков нету, то выведем сообщение об этом
                                            table_recycler.visibility = View.GONE
                                            no_tables_available.visibility = View.VISIBLE
                                        } else {
                                            Log.d("TOPKEK", t.body()!!.data.size.toString())
                                            initTableList()
                                            initRecycler()
                                        }
                                        it.table_id = tableId
                                    } else { // Если свободных столиков нету, то выведем сообщение об этом
                                        table_recycler.visibility = View.GONE
                                        no_tables_available.visibility = View.VISIBLE
                                    }
                                }

                                if (countComplite < 0){
                                    composite.clear()
                                }else if (countComplite == countCat){
                                    //посылаем сообщение что мы тут закончили
                                    emitter.onNext(true)
                                }
                                else countComplite++;
                            } else {
                                Log.i("check2", "${t.code()}")
                                countComplite = -5
                                composite.clear()
                                emitter.onError(OurException(t.code()))
                            }
                        }

                        override fun onError(e: Throwable) {

                        }

                    })!!
            )
        }
    }

    /**
     * Настраивает тулбар
     */
    private fun initToolBar() {
        toolbar.setNavigationIcon(R.drawable.ic_back_button) // Установим иконку в тулбаре
        toolbar.setNavigationOnClickListener(navigationClickListener) // Установим обработчик нажатий на тулбар
    }

    /**
     * Заполняет данными список столиков
     */
    private fun initTableList() {
        for (i in 0 until responseBody!!.data.size) {
            val tmp = responseBody!!.data[i]
            val table = Table(tmp.chair_count, tmp.position, tmp.chair_type, tmp.table_id)
            tableList.add(table)
        }
    }

    /**
     * Настраивает ресайклер вью
     */
    private fun initRecycler() {
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        table_recycler.layoutManager = layoutManager
        table_recycler.addItemDecoration(
            DividerItemDecoration(this, DividerItemDecoration.VERTICAL) // Разделитель элементов внутри ресайклера
        )
        table_recycler.adapter = AdapterTable(tableList, this)
    }

    /**
     * Реализация интерфейса для обработки нажатий вне адаптера
     * @param position Позиция элемента
     * @param isButtonClick Произошло ли нажатие на кнопку "выбрать"
     */
    override fun onTableClick(position: Int, isButtonClick: Boolean) {
        if (isButtonClick) { //Открываем новую активити
            val intent = Intent(this, PersonalInfoActivity::class.java)
            intent.putExtra("table_id", tableList[position].seatId)
            intent.putExtra("book_date_begin", newBooking.date)
            intent.putExtra("book_date_end", newBooking.date_to)
            intent.putExtra("book_time_begin", newBooking.time_from)
            intent.putExtra("book_time_end", newBooking.time_to)
            startActivity(intent)
        }
    }
}
