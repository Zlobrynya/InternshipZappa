package com.zlobrynya.internshipzappa

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText

class EntryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entry)
        val btn = findViewById<Button>(R.id.btnEnter)

        btn.setOnClickListener {
            val email = findViewById<EditText>(R.id.etEmailEntry)
            val pass = findViewById<EditText>(R.id.etPasswordEntry)
            val entryData = "login=" + email.text + "&password=" + pass.text
            PostLoginData.getInstance().postServer(entryData)


        }
    }
}
