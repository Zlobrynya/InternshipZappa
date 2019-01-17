package com.zlobrynya.internshipzappa.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import com.zlobrynya.internshipzappa.R
import com.zlobrynya.internshipzappa.adapter.AdapterListCategory
import com.zlobrynya.internshipzappa.tools.json.Dish
import com.zlobrynya.internshipzappa.tools.parcelable.CategoryParcelable
import kotlinx.android.synthetic.main.fragment_category_menu.*

class CategoryFragment: Fragment() {
    private lateinit var v: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        v = inflater.inflate(R.layout.fragment_category_menu,null)

        val categoryParcelable = arguments!!.getParcelable<CategoryParcelable>("Parcelable")
        val adapterListCategory = AdapterListCategory(context, R.id.listViewDish)
        //listViewDish.adapter = adapterListCategory
        val listView = v.findViewById<ListView>(R.id.listViewDish)
        listView.adapter = adapterListCategory
        var listtDish = categoryParcelable?.listMenu
        Log.i("CategoryFragment", listtDish?.size.toString())
        adapterListCategory.addAll(listtDish)
        adapterListCategory.notifyDataSetChanged()

        return v
    }


    companion object {
      fun newInstance(listDish: ArrayList<Dish>): Fragment{
          val fragment = CategoryFragment()
          val args = Bundle()
          val categoryParcelable = CategoryParcelable()
          categoryParcelable.listMenu = listDish
          args.putParcelable("Parcelable", categoryParcelable)
          fragment.arguments = args
          return fragment
      }
    }
}