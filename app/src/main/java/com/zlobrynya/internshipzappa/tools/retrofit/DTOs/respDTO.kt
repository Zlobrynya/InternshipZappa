package com.zlobrynya.internshipzappa.tools.retrofit.DTOs

import com.google.gson.annotations.SerializedName

data class respDTO(
    @SerializedName("code")var code: Int,
    @SerializedName("desc")var desc: String
)