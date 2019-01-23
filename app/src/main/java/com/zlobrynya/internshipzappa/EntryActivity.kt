package com.zlobrynya.internshipzappa

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_entry.*

class EntryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entry)
    }

    fun ChangeActivityReg(view: View){
        val change_activity = Intent(this, RegActivity::class.java)
        startActivity(change_activity)
    }

    fun ChangeActivityMenu(view: View){
        val change_activity = Intent(this, MainActivity::class.java)
        startActivity(change_activity)
    }

}
