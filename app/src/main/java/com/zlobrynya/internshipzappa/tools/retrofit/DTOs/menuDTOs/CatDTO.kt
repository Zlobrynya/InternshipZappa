package com.zlobrynya.internshipzappa.tools.retrofit.DTOs.menuDTOs

import com.google.gson.annotations.SerializedName

data class CatDTO(
    @SerializedName("class_id") val class_id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("order") val order: Int
)