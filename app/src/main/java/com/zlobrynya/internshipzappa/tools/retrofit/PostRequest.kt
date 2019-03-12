package com.zlobrynya.internshipzappa.tools.retrofit

import com.zlobrynya.internshipzappa.tools.retrofit.DTOs.bookingDTOs.bookingDataDTO
import com.zlobrynya.internshipzappa.tools.retrofit.DTOs.bookingDTOs.bookingUserDTO
import com.zlobrynya.internshipzappa.tools.retrofit.DTOs.bookingDTOs.respDTO
import com.zlobrynya.internshipzappa.tools.retrofit.DTOs.bookingDTOs.tableList
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface PostRequest {
    //запрос на свободные столы по указанным параметрам
    @Headers("Content-Type: application/json")
    @POST("empty_places")
    fun postBookingData(@Body book: bookingDataDTO): Call<tableList>

    //запрос на бронирование
    @Headers("Content-Type: application/json")
    @POST("reserve_place")
    fun postReserve(@Body reserve: bookingUserDTO): Call<respDTO>
}