package com.zlobrynya.narogah.tools.retrofit.DTOs.accountDTOs

import com.google.gson.annotations.SerializedName

data class passwordRecoveryDTO (
    @SerializedName("email") var email: String = "",
    @SerializedName("password") var password: String = "",
    @SerializedName("code") var code: String = ""
    )