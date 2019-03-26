package com.zlobrynya.internshipzappa.activity.booking

import android.content.Intent
import android.os.Bundle
import android.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.zlobrynya.internshipzappa.R
import com.zlobrynya.internshipzappa.activity.MainActivity
import com.zlobrynya.internshipzappa.activity.Menu2Activity
import com.zlobrynya.internshipzappa.activity.MenuActivity
import com.zlobrynya.internshipzappa.fragment.BookingContainerFragment
import com.zlobrynya.internshipzappa.fragment.BookingFragment
import com.zlobrynya.internshipzappa.fragment.PersonalInfoFragment
import com.zlobrynya.internshipzappa.fragment.TableSelectFragment
import kotlinx.android.synthetic.main.activity_end_booking.*
import io.reactivex.internal.util.BackpressureHelper.add
import kotlinx.android.synthetic.main.fragment_booking_container.*
import io.reactivex.internal.util.BackpressureHelper.add





class BookingEnd: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_end_booking)

        val code= intent.getIntExtra("code", 400)
        val na = intent.getStringExtra("name")
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
            val personalFragment = supportFragmentManager.findFragmentByTag("PERSONAL_INFO")
            val tableFragment = supportFragmentManager.findFragmentByTag("TABLE_SELECT")

            if (personalFragment != null){
                supportFragmentManager.beginTransaction()
                    .remove(personalFragment)
                    .commit()
            }
            if(tableFragment != null){
                supportFragmentManager.beginTransaction()
                    .remove(tableFragment)
                    .commit()
            }
        }
    }

}

