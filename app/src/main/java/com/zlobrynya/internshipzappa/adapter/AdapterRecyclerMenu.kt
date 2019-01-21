package com.zlobrynya.internshipzappa.adapter

import android.os.Build
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.zlobrynya.internshipzappa.R
import android.view.LayoutInflater
import com.zlobrynya.internshipzappa.tools.json.Dish


class AdapterRecyclerMenu(private val myDataset: ArrayList<Dish>): RecyclerView.Adapter<AdapterRecyclerMenu.Holder>() {
    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_menu, parent, false) as View
        return Holder(view)
    }

    override fun getItemCount(): Int {
        return myDataset.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.nameDish?.text = myDataset[position].name
    }


    class Holder(v: View) : RecyclerView.ViewHolder(v) {

        var nameDish: TextView? = null
        var imageView: ImageView? = null

        init {
            nameDish = v.findViewById(R.id.nameDish)
            imageView = v.findViewById(R.id.imageView)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                imageView!!.clipToOutline = true
            }
        }
    }
}