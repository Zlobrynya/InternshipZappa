package com.zlobrynya.internshipzappa.tools.retrofit

import com.zlobrynya.internshipzappa.tools.retrofit.DTOs.accountDTOs.*
import com.zlobrynya.internshipzappa.tools.retrofit.DTOs.bookingDTOs.*
import com.zlobrynya.internshipzappa.tools.retrofit.DTOs.respDTO
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.*

interface PostRequest {
    //запрос на свободные столы по указанным параметрам
    @Headers("Content-Type: application/json")
    @POST("empty_places")
    fun postBookingData(@Body book: bookingDataDTO): Observable<Response<tableList>>

    //запрос на бронирование
    @Headers("Content-Type: application/json")
    @POST("reserve_place")
    fun postReserve(@Header("Authorization") authorization: String, @Body reserve: bookingUserDTO): Observable<Response<respDTO>>

    //запрос на подтверждение мыла
    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("verify_email")
    fun postVerifyData(@Body verifyData: verifyEmailDTO): Observable<Response<verifyRespDTO>>

    //запрос на регистрацию
    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("reg_user")
    fun postRegData(@Body regData: regDTO): Observable<Response<regRespDTO>>

    //запрос на авторизацию
    @Headers("Content-Type: application/json")
    @POST("auth")
    fun postAuthData(@Body authData: authDTO): Observable<Response<authRespDTO>>

    //запрос на восстановление пароля
    @Headers("Content-Type: application/json")
    @POST("password_recovery")
    fun postPassRecData(@Body recoveryData: passwordRecoveryDTO): Observable<Response<respDTO>>

    //запрос на получение бронирований пользователя
    @Headers("Content-Type: application/json")
    @POST("show_user_booking")
    fun postUserBookings(@Header("Authorization") authorization: String, @Body userEmail: verifyEmailDTO): Observable<Response<UserBookingList>>

    //запрос на получение бронирований пользователя
    @Headers("Content-Type: application/json")
    @POST("delete_user_booking")
    fun postDeleteUserBookings(@Header("Authorization") authorization: String, @Body deleteBooking: deleteBookingDTO): Observable<Response<respDTO>>

    //запрос на получение данных пользователя
    @Headers("Content-Type: application/json")
    @POST("view_user_credentials")
    fun postViewUserCredentials(@Header("Authorization") authorization: String, @Body userEmail: verifyEmailDTO): Observable<Response<userDataDTO>>
}