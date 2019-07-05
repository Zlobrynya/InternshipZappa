package com.zappa.narogah.tools.retrofit.DTOs.menuDTOs

import com.google.gson.annotations.SerializedName

data class CatList(@SerializedName("categories") var categories: List<CatDTO>) {
}