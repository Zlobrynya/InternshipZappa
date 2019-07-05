package com.zlobrynya.narogah.tools.retrofit.DTOs.menuDTOs

import com.google.gson.annotations.SerializedName

data class DishClientDTO(
    @SerializedName("item_id") var item_id: Int = 0,
    @SerializedName("class_name") var class_name: String = "",
    @SerializedName("name") var name: String ="Без наименования",
    @SerializedName("price") var price: Double = 0.0,
    @SerializedName("photo") var photo: String = "null",
    @SerializedName("desc_short") var desc_short: String = "null",
    @SerializedName("desc_long") var desc_long: String = "null",
    @SerializedName("weight") var weight: String = "null",
    @SerializedName("recommended") var recommended: String = "null",
    @SerializedName("delivery") var delivery: String = "null",
    @SerializedName("sub_menu") var sub_menu: String = "null"
)