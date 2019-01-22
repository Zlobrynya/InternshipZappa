package com.zlobrynya.internshipzappa.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import com.zlobrynya.internshipzappa.R
import com.zlobrynya.internshipzappa.adapter.AdapterRecyclerMenu
import com.zlobrynya.internshipzappa.tools.json.Dish
import com.zlobrynya.internshipzappa.tools.parcelable.CategoryParcelable
import kotlinx.android.synthetic.main.fragment_category_menu.*

class CategoryFragment: Fragment() {
    private lateinit var v: View
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        v = inflater.inflate(R.layout.fragment_category_menu,null)

        val categoryParcelable = arguments!!.getParcelable<CategoryParcelable>("Parcelable")
        //arguments!!.remove("Parcelable")
        //val adapterListCategory = AdapterListMenu(context, R.id.listViewDish)
        /*listViewDish.adapter = adapterListCategory
        val listView = v.findViewById<ListView>(R.id.listViewDish)
        listView.adapter = adapterListCategory
        Log.i("CategoryFragment", listtDish?.size.toString())
        adapterListCategory.addAll(listtDish)
        adapterListCategory.notifyDataSetChanged()*/
        val listtDish = categoryParcelable?.listMenu
        viewAdapter = AdapterRecyclerMenu(listtDish!!, v.context!!)

        return v
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewManager = LinearLayoutManager(context)
        listRecyclerView.apply {
            // use a linear layout manager
            layoutManager = viewManager
            // specify an viewAdapter (see also next example)
            adapter = viewAdapter
            setHasFixedSize(true)
        }
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