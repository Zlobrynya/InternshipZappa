package com.zlobrynya.internshipzappa.tools.retrofit.DTOs.accountDTOs

import com.google.gson.annotations.SerializedName
import java.util.*

data class authRespDTO (
    @SerializedName("code") val code: Int,
    @SerializedName("desc") val password: String,
    @SerializedName("email") val email: String,
    @SerializedName("uuid") val uuid: String
)