package com.zlobrynya.internshipzappa.activity.booking

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.zlobrynya.internshipzappa.R
import com.zlobrynya.internshipzappa.activity.MenuActivity
import kotlinx.android.synthetic.main.activity_end_booking.*


class BookingEnd: AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_end_booking)

        val code= intent.getIntExtra("code", 400)
        val na = intent.getStringExtra("name")
        val phone = intent.getStringExtra("phone")
        when(code){
            200 -> {
                otmena.text = getString(R.string.prinyal, na)
                izmena.text = getString(R.string.po_nomeru, phone)
            }
            else -> {
                var text = getString(R.string.otclonena, na)
                otmena.text = text
                izmena.setText(R.string.izmeni)
                booking_end_phone.visibility = View.GONE
                booking_end_otmena.visibility = View.GONE
                perezvon.visibility = View.GONE
            }
        }

        btn_back.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MenuActivity::class.java)
        startActivity(intent)
    }
}