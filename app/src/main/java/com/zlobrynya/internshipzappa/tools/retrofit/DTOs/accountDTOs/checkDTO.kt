package com.zlobrynya.internshipzappa.tools.retrofit.DTOs.accountDTOs

import com.google.gson.annotations.SerializedName

data class checkDTO (
    @SerializedName("email") var email: String = "",
    @SerializedName("code") var code: String = ""
)