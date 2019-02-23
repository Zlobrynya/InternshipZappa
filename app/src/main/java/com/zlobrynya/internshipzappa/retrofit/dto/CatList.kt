package com.zlobrynya.internshipzappa.retrofit.dto

import com.google.gson.annotations.SerializedName

data class CatList(@SerializedName("categories") var categories: List<CatDTO>) {
}