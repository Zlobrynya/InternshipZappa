package com.zappa.narogah.adapter.menu

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.util.Log
import com.zappa.narogah.fragment.menu.CategoryFragment
import com.zappa.narogah.tools.retrofit.DTOs.menuDTOs.CatDTO

/*
 * Адаптер для TabLayout
 */

class AdapterTab(fm: FragmentManager?, val listDTO: List<CatDTO>, val pageCount: Int) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        if (position < listDTO.size)
            Log.i("countercat",position.toString())
            for( CatDTO in listDTO ){
                Log.i("countercat",CatDTO.name)
                if (CatDTO.order == position + 1)
                    return CategoryFragment.newInstance(CatDTO.name)
            }


        return Fragment::class.java.newInstance()
    }

    override fun getCount(): Int {
        return pageCount
    }
}