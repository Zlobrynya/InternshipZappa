package com.zlobrynya.internshipzappa.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zlobrynya.internshipzappa.R
import com.zlobrynya.internshipzappa.adapter.AdapterRecyclerMenu
import com.zlobrynya.internshipzappa.adapter.AdapterRecyclerViewTopping
import com.zlobrynya.internshipzappa.tools.database.MenuDB
import kotlinx.android.synthetic.main.fragment_category_menu.*

/*
*  Фрагмент для отображения списка блюд
*/

class CategoryFragment: Fragment() {
    private lateinit var v: View
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var menuDb: MenuDB

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        v = inflater.inflate(R.layout.fragment_category_menu,null)
        menuDb = MenuDB(v.context)

        val category = arguments!!.getString("category")
        //Log.i("CategoryFragment",category)
        val listDish = menuDb.getCategoryDish(category)
        //Log.i("CategoryFragment",listDish.size.toString())
        if(category != "Топинги") {
            viewAdapter = AdapterRecyclerMenu(listDish, v.context!!)
        }else{ viewAdapter = AdapterRecyclerViewTopping(listDish, v.context!!)}
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