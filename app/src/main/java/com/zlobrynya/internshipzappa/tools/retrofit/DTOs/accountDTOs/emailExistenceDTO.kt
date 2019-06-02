package com.zlobrynya.internshipzappa.tools.retrofit.DTOs.accountDTOs

import com.google.gson.annotations.SerializedName

data class emailExistenceDTO (
    @SerializedName("email") var email: String = ""
    )