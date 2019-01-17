package com.zlobrynya.internshipzappa.tools.json

import android.content.Context
import android.util.Log
import org.json.JSONArray
import org.json.JSONObject
import java.io.InputStream
import java.nio.charset.Charset

class ParsJson {
    lateinit var hotArray: ArrayList<Dish>
    lateinit var saladsArray: ArrayList<Dish>
    lateinit var soupArray: ArrayList<Dish>
    lateinit var burgerArray: ArrayList<Dish>
    lateinit var nonalcArray: ArrayList<Dish>
    lateinit var beerArray: ArrayList<Dish>


    constructor(context: Context){
        val inputStream = context.assets.open("menu.json")
        val byteArray = ByteArray(inputStream.available())

        inputStream.read(byteArray)
        inputStream.close()
        Log.i("JSON", String(byteArray, Charset.forName("windows-1251")))
        parsJson(String(byteArray, Charset.forName("windows-1251")))
    }

    private fun parsJson(strJson: String){
        val jsonObj = JSONObject(strJson)
        hotArray = getArray(jsonObj.getJSONArray("hot"))
        saladsArray = getArray(jsonObj.getJSONArray("salads"))
        soupArray = getArray(jsonObj.getJSONArray("soup"))
        burgerArray = getArray(jsonObj.getJSONArray("burger"))
        nonalcArray = getArray(jsonObj.getJSONArray("nonalc"))
        beerArray = getArray(jsonObj.getJSONArray("beer"))
    }

    private fun getArray(jsonArray: JSONArray): ArrayList<Dish> {
        var locArrayList = ArrayList<Dish>()
        for (i in 0..jsonArray.length()-1){
            val jsObject = jsonArray.get(i) as JSONObject
            locArrayList.add(Dish(jsObject.getString("name"),jsObject.getInt("price"),
                    jsObject.getString("descr"), jsObject.getString("weight")))
        }
        return locArrayList
    }
}

