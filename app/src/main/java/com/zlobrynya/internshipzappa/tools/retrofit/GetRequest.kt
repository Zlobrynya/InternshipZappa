package com.zlobrynya.internshipzappa.tools.retrofit

import com.zlobrynya.internshipzappa.tools.retrofit.dto.CatList
import com.zlobrynya.internshipzappa.tools.retrofit.dto.CheckDTO
import com.zlobrynya.internshipzappa.tools.retrofit.dto.DishList
import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Url


interface GetRequest {

    @GET("/categories")
    fun getAllCategories() : Observable<Response<CatList>>

    @GET("/menu/{category_id}")
    fun getAllDishes(@Path(value = "category_id", encoded = true) catId: String) : Observable<Response<DishList>>

    @GET("/check_update")
    fun getLogServer() : Observable<Response<CheckDTO>>

    /*@GET("/get_dish_count")
    fun getDishCount() : Observable<Response<ResponseBody>>*/

}