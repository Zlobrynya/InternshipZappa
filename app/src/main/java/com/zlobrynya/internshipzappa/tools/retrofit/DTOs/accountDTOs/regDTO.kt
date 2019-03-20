package com.zlobrynya.internshipzappa.tools.retrofit.DTOs.accountDTOs

import com.google.gson.annotations.SerializedName

data class regDTO (
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("code") val code: String,
    @SerializedName("name") val name: String,
    @SerializedName("phone") val phone: String
    )