package com.zlobrynya.internshipzappa.tools.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper


class Database(val context: Context) : SQLiteOpenHelper(context, DATABASE_NAME , null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {}

    //метод для обновление (ручного) бд
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {}

    fun deleteBD(){
        context.deleteDatabase(DATABASE_NAME)
    }

    companion object {
        val DATABASE_NAME = "bdOnTheHorns.db"
        private val DATABASE_VERSION = 1
    }
}