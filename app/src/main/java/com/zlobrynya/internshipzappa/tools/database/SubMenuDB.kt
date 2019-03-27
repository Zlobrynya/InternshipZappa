package com.zlobrynya.internshipzappa.tools.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.Cursor
import com.zlobrynya.internshipzappa.R
import com.zlobrynya.internshipzappa.tools.retrofit.DTOs.menuDTOs.DishDTO
import com.zlobrynya.internshipzappa.tools.retrofit.DTOs.menuDTOs.DishSubDTO


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

class SubMenuDB(val context: Context) {
    private val PARENT_NAME = "parent_name"
    private val NAME = "name"
    private val WEIGHT = "weight"
    private val PRICE = "price"
    private val NAME_TABLE = "sub_menu"
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
                NAME_TABLE + " (" + PARENT_NAME + " text not null, " +
                NAME + " text not null, " +
                WEIGHT + " text not null, " +
                PRICE + " integer);"
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
    fun addAllData(arrayDish: List<DishSubDTO>) {
        for (dish in arrayDish){
            addData(dish)
        }
    }

    //добавляем одну строку в бд
    fun addData(dish: DishSubDTO){
        if (dish.name.isEmpty())
            dish.name = context.getString(R.string.no_name)

        val query = "INSERT INTO " + NAME_TABLE + " VALUES(" + "\"" + dish.parent_name + "\",\"" +
                dish.name +  "\",\"" + dish.weight + "\"," + dish.price + ");"
        sqLiteDatabase!!.execSQL(query)
    }

    //вернет ArrayList<DescriptionDish> определенной категории
    fun getCategoryDish(category: String): ArrayList<DishSubDTO>{
        val query = "SELECT * FROM " + NAME_TABLE + " WHERE " + PARENT_NAME + "=\"" + category + "\""
        val cursor = sqLiteDatabase!!.rawQuery(query, null)
        val arrayDish = arrayListOf<DishSubDTO>()
        //cursor.moveToNext() - если больше строк нету то возвращает false
        while (cursor.moveToNext())
            arrayDish.add(getDish(cursor))
        cursor.close()
        return arrayDish
    }

    //получаем заполненный класс с бд
    private fun getDish(cursor: Cursor): DishSubDTO {
        val dish = DishSubDTO()
        dish.parent_name = cursor.getString(cursor.getColumnIndex(PARENT_NAME))
        dish.name = cursor.getString(cursor.getColumnIndex(NAME))
        dish.price = cursor.getInt(cursor.getColumnIndex(PRICE))
        dish.weight = cursor.getString(cursor.getColumnIndex(WEIGHT))
        return dish
    }

}