package com.zlobrynya.internshipzappa.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.zlobrynya.internshipzappa.R
import kotlinx.android.synthetic.main.activity_end_booking.*


class BookingEnd: AppCompatActivity() {

    val otvet: Boolean = false
    val na = "Илья"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_end_booking)

        when(otvet){
            true -> {
                var text = getString(R.string.prinyal, na)
                otmena.setText(text)
                izmena.setText(R.string.po_nomeru)
            }
            false -> {
                var text = getString(R.string.otclonena, na)
                otmena.setText(text)
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
}