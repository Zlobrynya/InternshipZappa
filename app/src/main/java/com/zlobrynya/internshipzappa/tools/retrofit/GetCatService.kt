package com.zlobrynya.internshipzappa.tools.retrofit

import com.zlobrynya.internshipzappa.tools.retrofit.dto.CatList
import com.zlobrynya.internshipzappa.tools.retrofit.dto.DishList
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url


interface GetCatService {

    @GET("/get_classes")
    fun getAllCategories() : Call<CatList>
}