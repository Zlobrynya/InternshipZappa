package com.zappa.narogah.fragment.menu

import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.zappa.narogah.R
import com.zappa.narogah.adapter.menu.AdapterTab
import com.zappa.narogah.tools.database.CategoryDB
import com.zappa.narogah.tools.retrofit.DTOs.menuDTOs.CatDTO
import kotlinx.android.synthetic.main.fragment_menu.*

/**
 * Фрагмент меню.
 *
 */
class MenuFragment : Fragment() {

    private lateinit var categoryDB: CategoryDB

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        categoryDB = CategoryDB(context!!)
        setCategories(categoryDB.getCategory())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_menu, container, false)
    }

    override fun onDestroy() {
        categoryDB.closeDataBase()
        super.onDestroy()
    }

    //устанавливаем в табы категориии
    private fun setCategories(categories: List<CatDTO>){
        Log.i("cat","$categories")
        viewPagerMenu.adapter =
            AdapterTab(childFragmentManager, categories, categories.size)
        viewPagerMenu.offscreenPageLimit = 6
        sliding_tabs.setupWithViewPager(viewPagerMenu)


        for (i in 0..categories.size){
            for (CatDTO in categories ){
                if (CatDTO.order == i + 1) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        sliding_tabs.getTabAt(i)?.text = CatDTO.name
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
    }
}
