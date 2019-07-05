package com.zlobrynya.narogah.tools.retrofit.DTOs.accountDTOs

import com.google.gson.annotations.SerializedName

data class verifyEmailDTO (
    @SerializedName("email") var email: String = ""
    )