package com.zlobrynya.internshipzappa.tools.retrofit.DTOs.accountDTOs

import com.google.gson.annotations.SerializedName

data class authDTO (
    @SerializedName("email") var email: String  = "",
    @SerializedName("password") var password: String = ""
)