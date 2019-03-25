package com.zlobrynya.internshipzappa.tools.retrofit.DTOs.accountDTOs

import com.google.gson.annotations.SerializedName

data class regDTO (
    @SerializedName("email") var email: String = "",
    @SerializedName("password") var password: String = "",
    @SerializedName("code") var code: String = "",
    @SerializedName("name") var name: String = "",
    @SerializedName("phone") var phone: String = ""
    )