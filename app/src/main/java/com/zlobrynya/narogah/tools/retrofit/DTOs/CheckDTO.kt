package com.zlobrynya.narogah.tools.retrofit.DTOs

import com.google.gson.annotations.SerializedName


data class CheckDTO(
    @SerializedName("count") var count: Int,
    @SerializedName("date") var date: String,
    @SerializedName("timetable_update") var time: String
)