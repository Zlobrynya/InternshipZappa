package com.zlobrynya.internshipzappa.tools.retrofit.DTOs.accountDTOs

import com.google.gson.annotations.SerializedName

data class regRespDTO (
    @SerializedName("code") var code: Int = 0,
    @SerializedName("desc") var desc: String = "",
    @SerializedName("access_token") var access_token: String = ""
)