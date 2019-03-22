package com.zlobrynya.internshipzappa.tools.retrofit.DTOs.accountDTOs

import com.google.gson.annotations.SerializedName
import java.util.*

data class authRespDTO (
    @SerializedName("code") var code: Int = 0,
    @SerializedName("desc") var password: String = "",
    @SerializedName("email") var email: String = "",
    @SerializedName("uuid") var uuid: String = ""
)