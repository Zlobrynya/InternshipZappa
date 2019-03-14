package com.zlobrynya.internshipzappa.tools.retrofit

import com.zlobrynya.internshipzappa.tools.retrofit.DTOs.menuDTOs.CatList
import com.zlobrynya.internshipzappa.tools.retrofit.DTOs.CheckDTO
import com.zlobrynya.internshipzappa.tools.retrofit.DTOs.bookingDTOs.bookingDataDTO
import com.zlobrynya.internshipzappa.tools.retrofit.DTOs.bookingDTOs.tableList
import com.zlobrynya.internshipzappa.tools.retrofit.DTOs.menuDTOs.DishList
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitClientInstance {

    private val BASE_URL = "https://na-rogah-api.herokuapp.com/api/v1/"
    private var getRequest: GetRequest? = null
    private var postRequest: PostRequest? = null

    init {
        val retrofit = Retrofit.Builder().baseUrl(BASE_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        getRequest = retrofit.create(GetRequest::class.java)
        postRequest = retrofit.create(PostRequest::class.java)
    }

    fun getAllCategories() : Observable<Response<CatList>> = getRequest!!.getAllCategories()

    fun getAllDishes(url: String) : Observable<Response<DishList>> = getRequest!!.getAllDishes(url)

    fun getLogServer() : Observable<Response<CheckDTO>> = getRequest!!.getLogServer()

    fun postBookingDate(book: bookingDataDTO) : Observable<Response<tableList>> = postRequest!!.postBookingData(book)

    //fun getDishCount() : Observable<Response<ResponseBody>> = getRequest!!.getDishCount()

    companion object {
        private var instance: RetrofitClientInstance? = null
        fun getInstance(): RetrofitClientInstance {
            if (instance == null)
                instance = RetrofitClientInstance()
            return instance as RetrofitClientInstance
        }
    }
}