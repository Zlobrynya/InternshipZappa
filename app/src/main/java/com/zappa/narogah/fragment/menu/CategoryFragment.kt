package com.zappa.narogah.fragment.menu

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zappa.narogah.R
import com.zappa.narogah.adapter.menu.AdapterRecyclerMenu
import com.zappa.narogah.adapter.menu.AdapterRecyclerViewTopping
import com.zappa.narogah.tools.database.MenuDB
import kotlinx.android.synthetic.main.fragment_category_menu.*

/*
*  Фрагмент для отображения списка блюд
*/

class CategoryFragment: Fragment() {
    private lateinit var v: View
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var menuDb: MenuDB

    @SuppressLint("InflateParams")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        v = inflater.inflate(R.layout.fragment_category_menu,null)
        menuDb = MenuDB(v.context)

        val category = arguments?.getString(getString(R.string.key_category))
        val listDish = menuDb.getCategoryDish(category!!)
        when (category){
            getString(R.string.category_topping) -> viewAdapter =
                AdapterRecyclerViewTopping(listDish, v.context!!)
            else -> viewAdapter = AdapterRecyclerMenu(listDish, v.context!!)
        }
        menuDb.closeDataBase()
        return v
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //настраиваем RecyclerView для отображение списка блюд
        viewManager = LinearLayoutManager(context)
        listRecyclerView.apply {
            layoutManager = viewManager
            adapter = viewAdapter
            setHasFixedSize(true)
        }
    }

    override fun onDestroy() {
        menuDb.closeDataBase()
        super.onDestroy()
    }


    companion object {
      fun newInstance(category: String): Fragment{
          //получаем данные с activity и загружаем в  Bundle
          val fragment = CategoryFragment()
          val args = Bundle()
          args.putString("category", category)
          fragment.arguments = args
          return fragment
      }
    }
}