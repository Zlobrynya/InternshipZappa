package com.zlobrynya.internshipzappa.tools.retrofit.DTOs.accountDTOs

import com.google.gson.annotations.SerializedName

data class changeUserDataDTO (
    @SerializedName("email") var email: String  = "",
    @SerializedName("new_email") var new_email: String  = "",
    @SerializedName("code") var code: Int  = 0,
    @SerializedName("name") var name: String  = "",
    @SerializedName("birthday") var birthday: String  = "",
    @SerializedName("phone") var phone: String  = ""
)