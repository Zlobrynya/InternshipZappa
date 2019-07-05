package com.zlobrynya.narogah.tools.retrofit.DTOs

import com.google.gson.annotations.SerializedName

data class respDTO(
    @SerializedName("code")var code: Int,
    @SerializedName("desc")var desc: String
)