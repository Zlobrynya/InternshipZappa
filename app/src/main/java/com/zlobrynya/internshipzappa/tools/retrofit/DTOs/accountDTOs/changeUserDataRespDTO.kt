package com.zlobrynya.internshipzappa.tools.retrofit.DTOs.accountDTOs

import com.google.gson.annotations.SerializedName

data class changeUserDataRespDTO (
    @SerializedName("code") var code: Int  = 0,
    @SerializedName("desc") var desc: String  = "",
    @SerializedName("email") var email: String  = ""
)