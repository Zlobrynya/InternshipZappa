package com.zlobrynya.internshipzappa.activity.booking

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.zlobrynya.internshipzappa.R
import com.zlobrynya.internshipzappa.activity.MainActivity
import com.zlobrynya.internshipzappa.activity.Menu2Activity
import com.zlobrynya.internshipzappa.activity.MenuActivity
import kotlinx.android.synthetic.main.activity_end_booking.*


class BookingEnd: AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_end_booking)
        supportActionBar!!.title = "Бронирование"

        val code= intent.getIntExtra("code", 400)
        val na = intent.getStringExtra("name")
        val phone = intent.getStringExtra("phone")
        when(code){
            200 -> {
                otmena.text = getString(R.string.prinyal, na)
                izmena.text = getString(R.string.recall)
            }
            else -> {
                otmena.text = getString(R.string.otclonena, na)
                izmena.text = getString(R.string.izmeni)
            }
        }

        btn_back.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, Menu2Activity::class.java)
        //intent.setFlags()
        startActivity(intent)
        finish()
    }
}