package com.zlobrynya.internshipzappa.tools.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.zlobrynya.internshipzappa.tools.retrofit.DTOs.bookingDTOs.visitingHoursDTO
import com.zlobrynya.internshipzappa.tools.retrofit.DTOs.menuDTOs.CatDTO

class VisitingHoursDB(context: Context) {
    private var database: Database? = null
    private var sqLiteDatabase: SQLiteDatabase? = null
    private val NAME_TABLE = "visiting_hours"
    private val TIMEFROM = "time_from"
    private val TIMETO = "time_to"
    private val WEEKDAY = "week_day"

    init {
        database = Database(context)
        sqLiteDatabase = database!!.writableDatabase
        createTable()
    }

    private fun createTable(){
        //запрос на создание табдлицы если ее не было
        val DATABASE_CREATE_SCRIPT = "create table if not exists " +
                NAME_TABLE + " (" + TIMEFROM + " text not null, " +
                TIMETO + " text not null, " +
                WEEKDAY + " text not null);"
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
    fun addAllData(arrayDay: List<visitingHoursDTO>) {
        for (day in arrayDay){
            addData(day)
        }
    }

    fun getCountRow(): Int{
        val query = "SELECT COUNT(*) FROM " + NAME_TABLE
        val statement = sqLiteDatabase!!.compileStatement(query)
        return statement.simpleQueryForLong().toInt()
    }

    //добавляем одну строку в бд
    fun addData(day: visitingHoursDTO){
        val query = "INSERT INTO " + NAME_TABLE + " VALUES(\"" + day.time_from + "\",\"" +
                day.time_to + "\",\"" + day.week_day + "\");"
        sqLiteDatabase!!.execSQL(query)
    }

    //вернет ArrayList<DescriptionDish> определенной категории
    fun getVisitingHours(): ArrayList<visitingHoursDTO>{
        val query = "SELECT * FROM " + NAME_TABLE
        val cursor = sqLiteDatabase!!.rawQuery(query, null)
        val arrayDay = arrayListOf<visitingHoursDTO>()
        //cursor.moveToNext() - если больше строк нету то возвращает false
        while (cursor.moveToNext()){
            val time_from = cursor.getString(cursor.getColumnIndex(TIMEFROM))
            val time_to = cursor.getString(cursor.getColumnIndex(TIMETO))
            val weekday = cursor.getString(cursor.getColumnIndex(WEEKDAY))
            val day = visitingHoursDTO(time_from, time_to, weekday)
            arrayDay.add(day)
        }
        cursor.close()
        return arrayDay
    }
}