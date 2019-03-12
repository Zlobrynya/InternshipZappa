package com.zlobrynya.internshipzappa.tools.retrofit.DTOs.bookingDTOs

import com.google.gson.annotations.SerializedName

data class respDTO(
    @SerializedName("code")val code: Int,
    @SerializedName("desc")val desc: String
)