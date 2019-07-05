package com.zlobrynya.narogah.tools.retrofit

import com.zlobrynya.narogah.tools.retrofit.DTOs.CheckDTO
import com.zlobrynya.narogah.tools.retrofit.DTOs.accountDTOs.userDataDTO
import com.zlobrynya.narogah.tools.retrofit.DTOs.bookingDTOs.UserBookingList
import com.zlobrynya.narogah.tools.retrofit.DTOs.bookingDTOs.visitingHoursList
import com.zlobrynya.narogah.tools.retrofit.DTOs.menuDTOs.CatList
import com.zlobrynya.narogah.tools.retrofit.DTOs.menuDTOs.DishList
import com.zlobrynya.narogah.tools.retrofit.DTOs.respDTO
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
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

    //тест jwt
    @GET("test_auth")
    fun getTestAuth(@Header("Authorization") authorization: String) : Observable<Response<respDTO>>


    //здесь должен будет быть запрос на определение статуса юзера
    @Headers("Content-Type: application/json")
    @GET("check_auth")
    fun getStatusData(@Header("Authorization") authorization: String): Observable<Response<respDTO>>

    //запрос на получение бронирований пользователя
    @Headers("Content-Type: application/json")
    @GET("show_user_booking")
    fun postUserBookings(@Header("Authorization") authorization: String): Observable<Response<UserBookingList>>

    //запрос на получение данных пользователя
    @Headers("Content-Type: application/json")
    @GET("view_user_credentials")
    fun postViewUserCredentials(@Header("Authorization") authorization: String): Observable<Response<userDataDTO>>
}