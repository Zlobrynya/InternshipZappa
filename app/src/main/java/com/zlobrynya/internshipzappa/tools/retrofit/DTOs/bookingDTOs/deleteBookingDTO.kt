package com.zlobrynya.internshipzappa.tools.retrofit.DTOs.bookingDTOs

import com.google.gson.annotations.SerializedName

data class deleteBookingDTO (
    @SerializedName("email") var email: String = "",
    @SerializedName("booking_id") var booking_id: Int = 0
    )