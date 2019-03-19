package com.zlobrynya.internshipzappa.tools.retrofit.DTOs.bookingDTOs

import com.google.gson.annotations.SerializedName

data class bookingDataDTO (
    @SerializedName("date") var date: String? = "",
    @SerializedName("time_from") var time_from: String? = "",
    @SerializedName("date_to") var date_to: String? = "",
    @SerializedName("time_to") var time_to: String? = ""
)