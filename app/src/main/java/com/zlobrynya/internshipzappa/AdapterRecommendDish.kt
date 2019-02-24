package com.zlobrynya.internshipzappa

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.nostra13.universalimageloader.core.ImageLoader
import com.zlobrynya.internshipzappa.tools.DescriptionDish

class AdapterRecommendDish(private val values: ArrayList<DescriptionDish>): RecyclerView.Adapter<AdapterRecommendDish.ViewHolder>() {

    override fun getItemCount() = values.size

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent?.context).inflate(R.layout.rect_recommend_dish, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder?.toping_price?.text = (values[position].price).toString()
        val imageLoader: ImageLoader = ImageLoader.getInstance()
        imageLoader.displayImage(values[position].photoUrl, holder.toping_photo)
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var toping_name: TextView? = null
        var toping_photo: ImageView? = null
        var toping_price: TextView? = null
        var toping_ves: TextView? = null
        init{
            toping_name = itemView?.findViewById(R.id.topingName)
            toping_photo = itemView?.findViewById(R.id.topingPhoto)
            toping_price = itemView?.findViewById(R.id.topingPrice)
            toping_ves = itemView?.findViewById(R.id.topingVes)
        }
    }
}