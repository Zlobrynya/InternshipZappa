package com.zlobrynya.internshipzappa

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class AdapterRecommendDish(private val values: List<String>): RecyclerView.Adapter<AdapterRecommendDish.ViewHolder>() {

    override fun getItemCount() = values.size

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent?.context).inflate(R.layout.rect_recommend_dish, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder?.textView?.text = values[position]
    }

    class ViewHolder(itemView: View?): RecyclerView.ViewHolder(itemView!!){
        var textView: TextView? = null
        init{
            textView = itemView?.findViewById(R.id.topingName)
        }
    }
}