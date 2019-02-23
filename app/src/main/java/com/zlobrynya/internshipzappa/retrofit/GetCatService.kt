package com.zlobrynya.internshipzappa.retrofit

import com.zlobrynya.internshipzappa.retrofit.dto.CatList
import retrofit2.Call
import retrofit2.http.GET


interface GetCatService {

    @GET("/get_classes")
    fun getAllCategories() : Call<CatList>
}