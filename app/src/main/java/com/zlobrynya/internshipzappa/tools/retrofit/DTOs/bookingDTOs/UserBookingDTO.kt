package com.zlobrynya.internshipzappa.tools.retrofit.DTOs.bookingDTOs

import com.google.gson.annotations.SerializedName

data class UserBookingDTO(
    @SerializedName("accepted") var accepted: Boolean = false,
    @SerializedName("booking_id") var booking_id: Int = 0,
    @SerializedName("date_time_from") var date_time_from: String = "",
    @SerializedName("date_time_from") var date_time_to: String = "",
    @SerializedName("table_id") var table_id: Int = 0,
    @SerializedName("seat_count") var seat_count: Int = 0, // Количество мест
    @SerializedName("seat_type") var seat_type: String = "", // Тип столика (стулья, диваны, бутылки..)
    @SerializedName("seat_place") var seat_place: String = "" // Место столика (у окна..)
)