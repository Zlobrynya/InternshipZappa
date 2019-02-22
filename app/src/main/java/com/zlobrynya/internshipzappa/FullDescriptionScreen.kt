package com.zlobrynya.internshipzappa

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView

class FullDescriptionScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.full_description_screen)
        //Создание RecyclerView
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.setLayoutManager(layoutManager)
        recyclerView.adapter = AdapterRecommendDish(generateValues())
    }

    private fun generateValues(): List<String> {
        val values = mutableListOf<String>()
        for (i in 0..3){
            values.add("Медово-горчичный соус, $i")
        }
        return values
    }

}
