package com.zlobrynya.internshipzappa.activity

import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.zlobrynya.internshipzappa.R
import com.zlobrynya.internshipzappa.adapter.AdapterTab
import kotlinx.android.synthetic.main.activity_menu.*
import android.util.Log
import com.zlobrynya.internshipzappa.tools.database.CategoryDB
import com.zlobrynya.internshipzappa.tools.retrofit.dto.CatDTO


class MenuActivity: AppCompatActivity() {
    private lateinit var categoryDB: CategoryDB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)
        supportActionBar!!.setTitle(R.string.menu_toolbar)
        supportActionBar!!.elevation = 0.0F

        categoryDB = CategoryDB(this)
        setCategories(categoryDB.getCategory())
    }


    override fun onDestroy() {
        categoryDB.closeDataBase()
        super.onDestroy()
    }

    //устанавливаем в табы категориии
    private fun setCategories(categories: List<CatDTO>){
        Log.i("cat","$categories")
        viewPagerMenu.adapter = AdapterTab(supportFragmentManager, categories, categories.size)
        Log.i("cat","$categories")
        sliding_tabs.setupWithViewPager(viewPagerMenu)
        for (i in 0..categories.size){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                sliding_tabs.getTabAt(i)?.text = categories.get(i).name
            }
        }
    }

    override fun onResume() {
        super.onResume()
    }
}

