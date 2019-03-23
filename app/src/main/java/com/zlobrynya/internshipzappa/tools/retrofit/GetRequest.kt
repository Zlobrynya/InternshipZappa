package com.zlobrynya.internshipzappa.tools.retrofit

import com.zlobrynya.internshipzappa.tools.retrofit.DTOs.menuDTOs.CatList
import com.zlobrynya.internshipzappa.tools.retrofit.DTOs.CheckDTO
import com.zlobrynya.internshipzappa.tools.retrofit.DTOs.bookingDTOs.visitingHoursDTO
import com.zlobrynya.internshipzappa.tools.retrofit.DTOs.bookingDTOs.visitingHoursList
import com.zlobrynya.internshipzappa.tools.retrofit.DTOs.menuDTOs.DishList
import com.zlobrynya.internshipzappa.tools.retrofit.DTOs.respDTO
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path


interface GetRequest {

    @GET("categories")
    fun getAllCategories() : Observable<Response<CatList>>

    @GET("menu/{category_id}")
    fun getAllDishes(@Path(value = "category_id", encoded = true) catId: String) : Observable<Response<DishList>>

    @GET("check_update")
    fun getLogServer() : Observable<Response<CheckDTO>>

    @GET("timetable")
    fun getTimeTable() : Observable<Response<visitingHoursList>>
    //проверка на есть ли юзер с таким мылом в бд на сервере
    @GET("find_user/{user_email}")
    fun getEmailExistence(@Path(value = "user_email", encoded = true) userEmail: String) : Observable<Response<respDTO>>

    @GET("find_user/{userEmail}")
    fun getUserExists(@Path(value = "userEmail", encoded = true) userEmail: String) : Observable<Response<respDTO>>

}