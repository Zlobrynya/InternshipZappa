package com.zlobrynya.internshipzappa.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.Cursor
import com.zlobrynya.internshipzappa.retrofit.dto.DishDTO
import com.zlobrynya.internshipzappa.tools.DescriptionDish


/*
* -dish_id (numer)
-title (string)
-price (number)
-photo_url (string)

Full description screen
-dish_id (numer)
-title (string)
-price (number)
-photo_url (string)
-description_long (string)
-description_short (string)
-weight (number)
-recommended (number, number, number)
*
* */

class MenuDB(context: Context) {
    private val DISH_ID = "dish_id"
    private val TITLE = "title"
    private val PRICE = "price"
    private val PHOTO_URL = "photo_url"
    private val DESC_LONG = "description_long"
    private val DESC_SHORT = "description_short"
    private val WEIGHT = "weight"
    private val CATEGORY = "category"

    //!!!!!!!!!!!!!!!!!!
    private val RECOMEND = "recommended"
    //!!!!!!!!!!!!!!!!
    private val NAME_TABLE = "menu"

    private var database: Database? = null
    private var sqLiteDatabase: SQLiteDatabase? = null

    init {
        database = Database(context)
        sqLiteDatabase = database!!.writableDatabase

        //запрос на создание табдлицы если ее не было
        var DATABASE_CREATE_SCRIPT = "create table if not exists " +
                NAME_TABLE + " (" + DISH_ID + " integer, " +
                TITLE + " text not null, " +
                PRICE + " Double, " +
                PHOTO_URL + " text not null, " +
                DESC_LONG + " text not null, " +
                DESC_SHORT + " text not null, " +
                WEIGHT + " text not null, " +
                CATEGORY + " integer, " +
                RECOMEND + " text not null);"
        //создается таблица
        sqLiteDatabase!!.execSQL(DATABASE_CREATE_SCRIPT)
    }



    //закрываем БД
    fun closeDataBase() {
        if (sqLiteDatabase!!.isOpen())
            sqLiteDatabase!!.close()
    }

    fun deleteDB(){
        closeDataBase()
        database!!.deleteBD()
    }

    //добавляем лист данных в бд
    fun addAllData(arrayDish: List<DishDTO>) {
        for (dish in arrayDish){
            addData(dish)
        }
    }

    //добавляем одну строку в бд
    fun addData(dish: DishDTO){
        val query = "INSERT INTO " + NAME_TABLE + " VALUES(" + dish.item_id + ",\"" +
                dish.name + "\"," + dish.price + ",\"" + dish.photo + "\",\"" +
                dish.desc_long + "\",\"" + dish.desc_short + "\",\"" + dish.weight + "\",\"" +
                dish.class_id + "\",\"" + dish.recommended + "\");"
        sqLiteDatabase!!.execSQL(query)
    }

    //получаем описание блюда
    fun getDescriptionDish(index: Int): DishDTO{
        val query = "SELECT * FROM " + NAME_TABLE + " WHERE " + DISH_ID + "=" + index
        val cursor = sqLiteDatabase!!.rawQuery(query, null)
        var dish = DishDTO()
        if(cursor.count != 0){
            cursor.moveToFirst()
            dish = getDish(cursor)
            dish = getDish(cursor)
        }
        cursor.close()
        return dish
    }

    //вернет ArrayList<DescriptionDish> определенной категории
    fun getCategoryDish(category: String): List<DishDTO>{
        val query = "SELECT * FROM " + NAME_TABLE + " WHERE " + CATEGORY + "=\"" + category + "\""
        val cursor = sqLiteDatabase!!.rawQuery(query, null)
        val arrayDish = arrayListOf<DishDTO>()
        //cursor.moveToNext() - если больше строк нету то возвращает false
        while (cursor.moveToNext())
            arrayDish.add(getDish(cursor))
        cursor.close()
        return arrayDish
    }

    //возвращает сколько всего строк в таблице
    fun getCountRow(): Int{
        val query = "SELECT COUNT(*) FROM " + NAME_TABLE
        val statement = sqLiteDatabase!!.compileStatement(query)
        return statement.simpleQueryForLong().toInt()
    }

    //получаем заполненный класс с бд
    private fun getDish(cursor: Cursor): DishDTO {
        val dish = DishDTO()
        dish.item_id = cursor.getInt(cursor.getColumnIndex(DISH_ID))
        dish.name = cursor.getString(cursor.getColumnIndex(TITLE))
        dish.desc_long = cursor.getString(cursor.getColumnIndex(DESC_LONG))
        dish.desc_short = cursor.getString(cursor.getColumnIndex(DESC_SHORT))
        dish.photo = cursor.getString(cursor.getColumnIndex(PHOTO_URL))
        dish.recommended = cursor.getString(cursor.getColumnIndex(RECOMEND))
        dish.class_id = cursor.getInt(cursor.getColumnIndex(CATEGORY))
        dish.price = cursor.getDouble(cursor.getColumnIndex(PRICE))
        dish.weight = cursor.getInt(cursor.getColumnIndex(WEIGHT))
        return dish
    }

}