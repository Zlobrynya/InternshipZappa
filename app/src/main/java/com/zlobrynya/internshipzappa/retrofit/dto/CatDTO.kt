package com.zlobrynya.internshipzappa.retrofit.dto

import com.google.gson.annotations.SerializedName

data class CatDTO(
    @SerializedName("class_id") val class_id: Int,
    @SerializedName("name") val name: String
)
