package com.zlobrynya.internshipzappa

import android.content.Intent
import android.graphics.Bitmap
import android.support.v7.view.menu.MenuView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.assist.FailReason
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener
import com.zlobrynya.internshipzappa.tools.DescriptionDish
import kotlinx.android.synthetic.main.rect_recommend_dish.*


class AdapterRecommendDish(private val values: ArrayList<DescriptionDish>): RecyclerView.Adapter<AdapterRecommendDish.ViewHolder>() {

    override fun getItemCount() = values.size

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.rect_recommend_dish, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.toping_price?.text = (values[position].price).toString()
        val imageLoader: ImageLoader = ImageLoader.getInstance()
        imageLoader.displayImage(values[position].photoUrl, holder.toping_photo, object: ImageLoadingListener{
            override fun onLoadingComplete(imageUri: String?, view: View?, loadedImage: Bitmap?) {
                holder.spinner?.visibility = View.GONE
            }

            override fun onLoadingStarted(imageUri: String?, view: View?) {
                imageLoader.displayImage("drawable://"+R.drawable.noimage, holder.toping_photo)
                holder.spinner?.visibility = View.VISIBLE
            }

            override fun onLoadingCancelled(imageUri: String?, view: View?) {
                imageLoader.displayImage("drawable://"+R.drawable.noimage, holder.toping_photo)
            }

            override fun onLoadingFailed(imageUri: String?, view: View?, failReason: FailReason?) {
                imageLoader.displayImage("drawable://"+R.drawable.noimage, holder.toping_photo)
            }

        })
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var i: Int = 1
        var toping_name: TextView? = null
        var toping_photo: ImageView? = null
        var toping_price: TextView? = null
        var toping_ves: TextView? = null
        var spinner: ProgressBar? = null
        var btn_toping: Button? = null
        var btn_plus: Button? = null
        var btn_minus: Button? = null
        var tv_counter: TextView? = null
        init{
            toping_name = itemView.findViewById(R.id.topingName)
            toping_photo = itemView.findViewById(R.id.topingPhoto)
            toping_price = itemView.findViewById(R.id.topingPrice)
            toping_ves = itemView.findViewById(R.id.topingVes)
            spinner = itemView.findViewById(R.id.progressBar2)
            btn_toping = itemView.findViewById(R.id.btnToping)
            btn_plus = itemView.findViewById(R.id.btnPlus)
            btn_minus = itemView.findViewById(R.id.btnMinus)
            tv_counter = itemView.findViewById(R.id.tvCounter)
            btn_toping?.setOnClickListener {
                btn_toping?.visibility = View.GONE
                btn_plus?.visibility = View.VISIBLE
                btn_minus?.visibility = View.VISIBLE
                tv_counter?.visibility = View.VISIBLE
            }
            btn_plus?.setOnClickListener{
                tv_counter?.text = (++i).toString()
            }
            btn_minus?.setOnClickListener{
                tv_counter?.text = (--i).toString()
            }
        }
    }
}