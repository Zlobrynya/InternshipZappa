package com.zlobrynya.internshipzappa.activity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.nostra13.universalimageloader.core.ImageLoader
import com.zlobrynya.internshipzappa.R


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

       // val intent = Intent(this, MenuActivity::class.java)
       // startActivity(intent)
    }

    fun clockButton(v: View){
        val intent = Intent(this, MenuActivity::class.java)
        startActivity(intent)
        finish()
    }
}
