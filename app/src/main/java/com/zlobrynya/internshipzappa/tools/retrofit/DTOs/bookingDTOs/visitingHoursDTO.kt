package com.zlobrynya.internshipzappa.tools.retrofit.DTOs.bookingDTOs

import com.google.gson.annotations.SerializedName

class visitingHoursDTO (
    @SerializedName("time_from") var time_from: String = "",
    @SerializedName("time_to") var time_to: String = "",
    @SerializedName("week_day") var week_day: String = ""
)