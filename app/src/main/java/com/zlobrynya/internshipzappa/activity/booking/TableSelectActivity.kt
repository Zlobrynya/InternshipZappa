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
import com.zlobrynya.internshipzappa.tools.retrofit.DTOs.bookingDTOs.bookingDataDTO
import com.zlobrynya.internshipzappa.tools.retrofit.DTOs.bookingDTOs.tableDTO
import com.zlobrynya.internshipzappa.tools.retrofit.DTOs.bookingDTOs.tableList
import com.zlobrynya.internshipzappa.tools.retrofit.PostRequest
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
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
        networkPost()
        //initTableList()
        //initRecycler()
    }

    /**
     * Заполняет объект для POST запрос
     */
    private fun initRequest() {
        val bookDateBegin = intent.getStringExtra("book_date_begin")
        val bookTimeBegin = intent.getStringExtra("book_time_begin")
        val bookTimeEnd = intent.getStringExtra("book_time_end")
        newBooking.date = bookDateBegin
        newBooking.time_from = bookTimeBegin
        newBooking.time_to = bookTimeEnd
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
        requestCall.enqueue(object : Callback<tableList> {
            override fun onFailure(call: Call<tableList>, t: Throwable) {}

            override fun onResponse(call: Call<tableList>, response: Response<tableList>) {
                if (response.isSuccessful) {
                    Log.i("check1", "${response.code()}")
                    responseBody = response.body()
                    if (responseBody != null) {
                        Log.d("TOPKEK", responseBody!!.data.size.toString())
                        initTableList()
                        initRecycler()
                    }
                } else {
                    Log.i("check2", "${response.code()}")
                }
            }
        })
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
            intent.putExtra("book_time_begin", newBooking.time_from)
            intent.putExtra("book_time_end", newBooking.time_to)
            startActivity(intent)
        }
    }
}
