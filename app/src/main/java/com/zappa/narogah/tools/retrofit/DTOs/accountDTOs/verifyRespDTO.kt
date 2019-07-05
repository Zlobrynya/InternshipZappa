package com.zappa.narogah.tools.retrofit.DTOs.accountDTOs

import com.google.gson.annotations.SerializedName

data class verifyRespDTO (
    @SerializedName("code")var code: Int,
    @SerializedName("desc")var desc: String,
    @SerializedName("email_code")var email_code: String
)