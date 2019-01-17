package com.zlobrynya.internshipzappa.activity

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v7.app.AppCompatActivity
import com.zlobrynya.internshipzappa.R
import com.zlobrynya.internshipzappa.fragment.CategoryFragment
import com.zlobrynya.internshipzappa.tools.json.ParsJson
import kotlinx.android.synthetic.main.activity_menu.*

class MenuActivity: AppCompatActivity() {

    lateinit var parsJson: ParsJson

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        //Get json data from file
        parsJson = ParsJson(this)

        //val adapter = AdapterPageView(supportFragmentManager,4)
        viewPagerMenu.adapter = AdapterPageView(supportFragmentManager,6, parsJson)

    }

    class AdapterPageView(val gm: FragmentManager, val countF: Int, val parsJson: ParsJson): FragmentPagerAdapter(gm) {
        override fun getItem(position: Int): Fragment? {
            when(position){
                0 -> return CategoryFragment.newInstance(parsJson.hotArray)
                1 -> return CategoryFragment.newInstance(parsJson.saladsArray)
                2 -> return CategoryFragment.newInstance(parsJson.soupArray)
                3 -> return CategoryFragment.newInstance(parsJson.nonalcArray)
                4 -> return CategoryFragment.newInstance(parsJson.burgerArray)
                5 -> return CategoryFragment.newInstance(parsJson.beerArray)
            }
            return Fragment::class.java.newInstance()
        }

        override fun getCount(): Int = countF
    }
}