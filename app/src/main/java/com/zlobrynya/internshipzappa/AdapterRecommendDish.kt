package com.zlobrynya.internshipzappa

import android.content.Intent
import android.graphics.Bitmap
import android.support.v7.view.menu.MenuView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.assist.FailReason
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener
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
        imageLoader.displayImage(values[position].photoUrl, holder.toping_photo, object: ImageLoadingListener{
            override fun onLoadingComplete(imageUri: String?, view: View?, loadedImage: Bitmap?) {
                holder?.spinner?.visibility = View.GONE
            }

            override fun onLoadingStarted(imageUri: String?, view: View?) {
                holder?.spinner?.visibility = View.VISIBLE
            }

            override fun onLoadingCancelled(imageUri: String?, view: View?) {
            }

            override fun onLoadingFailed(imageUri: String?, view: View?, failReason: FailReason?) {
            }

        })
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var toping_name: TextView? = null
        var toping_photo: ImageView? = null
        var toping_price: TextView? = null
        var toping_ves: TextView? = null
        var spinner: View? = null
        var btn_toping: View? = null
        init{
            toping_name = itemView?.findViewById(R.id.topingName)
            toping_photo = itemView?.findViewById(R.id.topingPhoto)
            toping_price = itemView?.findViewById(R.id.topingPrice)
            toping_ves = itemView?.findViewById(R.id.topingVes)
            spinner = itemView?.findViewById(R.id.progressBar2)
            btn_toping = itemView?.findViewById(R.id.btnToping)
        }
    }
}