package com.zlobrynya.internshipzappa.retrofit.dto

import com.google.gson.annotations.SerializedName

data class DishDTO(
    @SerializedName("item_id") var item_id: Int = 0,
    @SerializedName("class_id") var class_id: Int = 0,
    @SerializedName("name") var name: String ="",
    @SerializedName("price") var price: Double = 0.0,
    @SerializedName("photo") var photo: String = "",
    @SerializedName("desc_short") var desc_short: String = "",
    @SerializedName("desc_long") var desc_long: String = "",
    @SerializedName("weight") var weight: Int = 0,
    @SerializedName("recommended") var recommended: String = ""
)