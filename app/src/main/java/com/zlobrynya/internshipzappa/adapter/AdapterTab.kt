package com.zlobrynya.internshipzappa.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.zlobrynya.internshipzappa.fragment.CategoryFragment
import com.zlobrynya.internshipzappa.tools.MenuDish

/*
 * Адаптер для TabLayout
 */

class AdapterTab(fm: FragmentManager?, val menuDish: MenuDish, val pageCount: Int) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        when(position){
            0 -> return CategoryFragment.newInstance(menuDish.hotArray)
            1 -> return CategoryFragment.newInstance(menuDish.saladsArray)
            2 -> return CategoryFragment.newInstance(menuDish.soupArray)
            3 -> return CategoryFragment.newInstance(menuDish.nonalcArray)
            4 -> return CategoryFragment.newInstance(menuDish.burgerArray)
            5 -> return CategoryFragment.newInstance(menuDish.beerArray)
        }
        return Fragment::class.java.newInstance()
    }

    override fun getCount(): Int {
        return pageCount
    }
}