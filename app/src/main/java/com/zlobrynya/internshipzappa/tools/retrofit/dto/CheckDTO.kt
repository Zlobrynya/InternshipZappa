package com.zlobrynya.internshipzappa.tools.retrofit.dto

import com.google.gson.annotations.SerializedName


data class CheckDTO(
    @SerializedName("count") var count: Int,
    @SerializedName("date") var date: String
)