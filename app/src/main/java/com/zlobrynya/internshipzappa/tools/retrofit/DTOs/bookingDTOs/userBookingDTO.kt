package com.zlobrynya.internshipzappa.tools.retrofit.DTOs.bookingDTOs

import com.google.gson.annotations.SerializedName

data class userBookingDTO (
    @SerializedName("accepted") var accepted: Boolean = false,
    @SerializedName("booking_id") var booking_id: String = "",
    @SerializedName("date_time_from") var date_time_from: String = "",
    @SerializedName("date_time_from") var date_time_to: String = "",
    @SerializedName("table_id") var table_id: Int = 0
    )