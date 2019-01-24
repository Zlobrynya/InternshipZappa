package com.zlobrynya.internshipzappa

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun ChangeActivityEntry(view: View){
        val change_activity = Intent(this, EntryActivity::class.java)
        startActivity(change_activity)
    }
}
