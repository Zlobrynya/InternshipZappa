package com.zlobrynya.narogah.tools.retrofit.DTOs.menuDTOs

import com.google.gson.annotations.SerializedName

data class DishSubDTO (
    @SerializedName("parent_name") var parent_name: String = "",
    @SerializedName("name") var name: String = "",
    @SerializedName("weight") var weight: String = "",
    @SerializedName("price") var price: Int = 0
    )