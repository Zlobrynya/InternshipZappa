package com.zlobrynya.internshipzappa.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.util.Log
import com.zlobrynya.internshipzappa.fragment.CategoryFragment
import com.zlobrynya.internshipzappa.retrofit.dto.CatDTO
import com.zlobrynya.internshipzappa.tools.MenuDish

/*
 * Адаптер для TabLayout
 */

class AdapterTab(fm: FragmentManager?, val listDTO: List<CatDTO>, val pageCount: Int) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        Log.i("categories", listDTO.get(position).name)
        if (position < listDTO.size)
            return CategoryFragment.newInstance(listDTO.get(position).name)

        return Fragment::class.java.newInstance()
    }

    override fun getCount(): Int {
        return pageCount
    }
}