package com.zlobrynya.narogah.tools.retrofit

import com.zlobrynya.narogah.tools.retrofit.DTOs.accountDTOs.*
import com.zlobrynya.narogah.tools.retrofit.DTOs.bookingDTOs.bookingDataDTO
import com.zlobrynya.narogah.tools.retrofit.DTOs.bookingDTOs.bookingUserDTO
import com.zlobrynya.narogah.tools.retrofit.DTOs.bookingDTOs.tableList
import com.zlobrynya.narogah.tools.retrofit.DTOs.respDTO
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
    @DELETE("delete_user_booking/{booking_id}")
    fun postDeleteUserBookings(@Header("Authorization") authorization: String, @Path(value = "booking_id", encoded = true) bookingId: String): Observable<Response<respDTO>>

    //запрос на изменение данных пользователя
    @Headers("Content-Type: application/json")
    @PATCH("change_user_credentials")
    fun postChangeUserCredentials(@Header("Authorization") authorization: String, @Body userChangedData: changeUserDataDTO): Observable<Response<changeUserDataRespDTO>>
}