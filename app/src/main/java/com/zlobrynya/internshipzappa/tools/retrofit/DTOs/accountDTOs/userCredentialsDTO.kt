package com.zlobrynya.internshipzappa.tools.retrofit.DTOs.accountDTOs

import com.google.gson.annotations.SerializedName

data class userCredentialsDTO(
    @SerializedName("birthday") var birthday: String? = null,
    @SerializedName("email") var email: String = "",
    @SerializedName("name") var name: String = "",
    @SerializedName("phone") var phone: String = "",
    @SerializedName("reg_date") var reg_date: String = ""
)