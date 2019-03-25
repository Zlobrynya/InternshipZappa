package com.zlobrynya.internshipzappa.tools.retrofit

import com.zlobrynya.internshipzappa.tools.retrofit.DTOs.accountDTOs.*
import com.zlobrynya.internshipzappa.tools.retrofit.DTOs.bookingDTOs.bookingDataDTO
import com.zlobrynya.internshipzappa.tools.retrofit.DTOs.bookingDTOs.bookingUserDTO
import com.zlobrynya.internshipzappa.tools.retrofit.DTOs.respDTO
import com.zlobrynya.internshipzappa.tools.retrofit.DTOs.bookingDTOs.tableList
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
    fun postReserve(@Body reserve: bookingUserDTO): Observable<Response<respDTO>>

    //запрос на подтверждение мыла
    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("verify_email")
    fun postVerifyData(@Body verifyData: verifyEmailDTO): Observable<Response<verifyRespDTO>>

    //запрос на регистрацию
    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("reg_user")
    fun postRegData(@Body regData: regDTO): Observable<Response<respDTO>>

    //запрос на авторизацию
    @Headers("Content-Type: application/json")
    @POST("auth")
    fun postAuthData(@Body authData: authDTO): Observable<Response<authRespDTO>>

    //запрос на восстановление пароля
    @Headers("Content-Type: application/json")
    @POST("password_recovery")
    fun postPassRecData(@Body recoveryData: passwordRecoveryDTO): Observable<Response<respDTO>>

}