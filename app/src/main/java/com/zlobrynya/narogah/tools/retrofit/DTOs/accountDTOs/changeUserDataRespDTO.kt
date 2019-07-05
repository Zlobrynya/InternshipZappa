package com.zlobrynya.narogah.tools.retrofit.DTOs.accountDTOs

import com.google.gson.annotations.SerializedName

data class changeUserDataRespDTO (
    @SerializedName("access_token") var access_token: String = "",
    @SerializedName("code") var code: Int  = 0,
    @SerializedName("desc") var desc: String  = "",
    @SerializedName("email") var email: String  = ""
)