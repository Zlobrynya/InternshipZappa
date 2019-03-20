package com.zlobrynya.internshipzappa.tools.retrofit.DTOs.accountDTOs

import com.google.gson.annotations.SerializedName

data class authDTO (
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)