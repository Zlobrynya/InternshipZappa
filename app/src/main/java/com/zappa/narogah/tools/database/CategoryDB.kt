package com.zappa.narogah.tools.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.zappa.narogah.tools.retrofit.DTOs.menuDTOs.CatDTO

class CategoryDB(context: Context) {
    private var database: Database? = null
    private var sqLiteDatabase: SQLiteDatabase? = null
    private val NAME_TABLE = "category"
    private val CAT_ID = "cat_id"
    private val CATEGORY = "category"
    private val CORDER = "corder"

    init {
        database = Database(context)
        sqLiteDatabase = database!!.writableDatabase
        createTable()
    }

    private fun createTable(){
        //запрос на создание табдлицы если ее не было
        val DATABASE_CREATE_SCRIPT = "create table if not exists " +
                NAME_TABLE + " (" + CAT_ID + " integer, " +
                CATEGORY + " text not null, " +
                CORDER + " integer);"
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

    //очищает таблицу
    fun clearTableDB(){
        sqLiteDatabase!!.execSQL("delete from "+ NAME_TABLE);
        createTable()
    }


    //добавляем лист данных в бд
    fun addAllData(arrayDish: List<CatDTO>) {
        for (dish in arrayDish){
            addData(dish)
        }
    }

    //добавляем одну строку в бд
    fun addData(dish: CatDTO){
        val query = "INSERT INTO " + NAME_TABLE + " VALUES(" + dish.class_id + ",\"" +
                dish.name + "\"," + dish.order + ");"
        sqLiteDatabase!!.execSQL(query)
    }

    //вернет ArrayList<DescriptionDish> определенной категории
    fun getCategory(): ArrayList<CatDTO>{
        val query = "SELECT * FROM " + NAME_TABLE
        val cursor = sqLiteDatabase!!.rawQuery(query, null)
        val arrayDish = arrayListOf<CatDTO>()
        //cursor.moveToNext() - если больше строк нету то возвращает false
        while (cursor.moveToNext()){
            val class_id = cursor.getInt(cursor.getColumnIndex(CAT_ID))
            val name = cursor.getString(cursor.getColumnIndex(CATEGORY))
            val corder = cursor.getInt(cursor.getColumnIndex(CORDER))
            val dish = CatDTO(class_id, name, corder)
            arrayDish.add(dish)
        }
        cursor.close()
        return arrayDish
    }


}