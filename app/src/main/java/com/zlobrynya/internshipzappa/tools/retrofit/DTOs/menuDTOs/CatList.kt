package com.zlobrynya.internshipzappa.tools.retrofit.DTOs.menuDTOs

import com.google.gson.annotations.SerializedName
import com.zlobrynya.internshipzappa.tools.retrofit.DTOs.menuDTOs.CatDTO

data class CatList(@SerializedName("categories") var categories: List<CatDTO>) {
}