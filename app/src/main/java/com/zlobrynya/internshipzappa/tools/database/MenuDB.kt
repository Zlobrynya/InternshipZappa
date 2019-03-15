package com.zlobrynya.internshipzappa.tools.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.Cursor
import com.zlobrynya.internshipzappa.R
import com.zlobrynya.internshipzappa.tools.retrofit.DTOs.menuDTOs.DishDTO


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

class MenuDB(val context: Context) {
    private val DISH_ID = "dish_id"
    private val TITLE = "title"
    private val PRICE = "price"
    private val PHOTO_URL = "photo_url"
    private val DESC_LONG = "description_long"
    private val DESC_SHORT = "description_short"
    private val WEIGHT = "weight"
    private val CATEGORY = "category"
    private val RECOMEND = "recommended"
    private val DELIVERY = "delivery"
    private val NAME_TABLE = "menu"
    private var database: Database? = null
    private var sqLiteDatabase: SQLiteDatabase? = null

    init {
        database = Database(context)
        sqLiteDatabase = database!!.writableDatabase
        createTable()
    }

    private fun createTable(){
        //запрос на создание табдлицы если ее не было
        val DATABASE_CREATE_SCRIPT = "create table if not exists " +
                NAME_TABLE + " (" + DISH_ID + " integer, " +
                TITLE + " text not null, " +
                PRICE + " Double, " +
                PHOTO_URL + " text not null, " +
                DESC_LONG + " text not null, " +
                DESC_SHORT + " text not null, " +
                WEIGHT + " text not null, " +
                CATEGORY + " text not null, " +
                RECOMEND + " text not null, " +
                DELIVERY + " text not null);"
        //создается таблица
        sqLiteDatabase!!.execSQL(DATABASE_CREATE_SCRIPT)
    }

    //закрываем БД
    fun closeDataBase() {
        if (sqLiteDatabase!!.isOpen())
            sqLiteDatabase!!.close()
    }

    //очищает таблицу
    fun clearTableDB(){
        sqLiteDatabase!!.execSQL("delete from "+ NAME_TABLE);
        createTable()
    }

    //добавляем лист данных в бд
    fun addAllData(arrayDish: List<DishDTO>) {
        for (dish in arrayDish){
            addData(dish)
        }
    }

    //добавляем одну строку в бд
    fun addData(dish: DishDTO){
        if (dish.name.isEmpty())
            dish.name = context.getString(R.string.no_name)
        if (dish.photo.isEmpty())
            dish.photo = "null"

        val query = "INSERT INTO " + NAME_TABLE + " VALUES(" + dish.item_id + ",\"" +
                dish.name +  "\"," + dish.price + ",\"" + dish.photo + "\",\"" +
                dish.desc_long + "\",\"" + dish.desc_short + "\",\"" + dish.weight + "\",\"" +
                dish.class_name + "\",\"" + dish.recommended + "\",\"" + dish.delivery + "\");"
        sqLiteDatabase!!.execSQL(query)
    }

    //получаем описание блюда
    fun getDescriptionDish(index: Int): DishDTO {
        val query = "SELECT * FROM " + NAME_TABLE + " WHERE " + DISH_ID + "=" + index
        val cursor = sqLiteDatabase!!.rawQuery(query, null)
        var dish = DishDTO()
        if(cursor.count != 0){
            cursor.moveToFirst()
            dish = getDish(cursor)
        }
        cursor.close()
        return dish
    }

    //вернет ArrayList<DescriptionDish> определенной категории
    fun getCategoryDish(category: String): ArrayList<DishDTO>{
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
        dish.class_name = cursor.getString(cursor.getColumnIndex(CATEGORY))
        dish.price = cursor.getDouble(cursor.getColumnIndex(PRICE))
        dish.weight = cursor.getString(cursor.getColumnIndex(WEIGHT))
        dish.delivery = cursor.getString(cursor.getColumnIndex(DELIVERY))
        return dish
    }

}