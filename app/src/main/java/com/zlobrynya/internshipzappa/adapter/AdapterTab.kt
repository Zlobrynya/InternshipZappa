package com.zlobrynya.internshipzappa.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.util.Log
import com.zlobrynya.internshipzappa.fragment.CategoryFragment
import com.zlobrynya.internshipzappa.tools.retrofit.dto.CatDTO

/*
 * Адаптер для TabLayout
 */

class AdapterTab(fm: FragmentManager?, val listDTO: List<CatDTO>, val pageCount: Int) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        if (position < listDTO.size)
            return CategoryFragment.newInstance(listDTO.get(position).name)

        return Fragment::class.java.newInstance()
    }

    override fun getCount(): Int {
        return pageCount
    }
}