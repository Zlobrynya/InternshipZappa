package com.zlobrynya.internshipzappa.adapter

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.nostra13.universalimageloader.core.DisplayImageOptions
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.assist.FailReason
import com.nostra13.universalimageloader.core.assist.ImageScaleType
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener
import com.zlobrynya.internshipzappa.R
import com.zlobrynya.internshipzappa.tools.retrofit.dto.DishDTO


class AdapterRecommendDish(private val values: ArrayList<DishDTO>): RecyclerView.Adapter<AdapterRecommendDish.ViewHolder>() {
    lateinit var options: DisplayImageOptions

    override fun getItemCount() = values.size

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.rect_recommend_dish, parent, false)
        options = DisplayImageOptions.Builder()
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
            .build()
        return ViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val imageLoader: ImageLoader = ImageLoader.getInstance()
        //Проверка получены ли данные

        holder.toping_price?.text = if (values[position].price == 0.0) "-"
            else values[position].price.toInt().toString() + " \u20BD"

        if (values[position].weight == 0){
            holder.toping_ves?.visibility = View.GONE
        } else{
            holder.toping_ves?.text = values[position].weight.toString()
        }

        holder.toping_name?.text = values[position].name


        imageLoader.displayImage(values[position].photo, holder.toping_photo, options, object: ImageLoadingListener{
            override fun onLoadingComplete(imageUri: String?, view: View?, loadedImage: Bitmap?) {
                holder.spinner?.visibility = View.GONE
            }

            override fun onLoadingStarted(imageUri: String?, view: View?) {
                holder.spinner?.visibility = View.VISIBLE
            }

            override fun onLoadingCancelled(imageUri: String?, view: View?) {
                holder.spinner?.visibility = View.GONE
                imageLoader.displayImage("drawable://"+ R.drawable.no_toping, holder.toping_photo)
            }

            override fun onLoadingFailed(imageUri: String?, view: View?, failReason: FailReason?) {
                holder.spinner?.visibility = View.GONE
                imageLoader.displayImage("drawable://"+ R.drawable.no_toping, holder.toping_photo)
            }

        })
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
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
            toping_name = itemView.findViewById(com.zlobrynya.internshipzappa.R.id.topingName)
            toping_photo = itemView.findViewById(com.zlobrynya.internshipzappa.R.id.topingPhoto)
            toping_price = itemView.findViewById(com.zlobrynya.internshipzappa.R.id.topingPrice)
            toping_ves = itemView.findViewById(com.zlobrynya.internshipzappa.R.id.topingVes)
            spinner = itemView.findViewById(com.zlobrynya.internshipzappa.R.id.progressBar2)
            btn_toping = itemView.findViewById(com.zlobrynya.internshipzappa.R.id.btnToping)
            btn_plus = itemView.findViewById(com.zlobrynya.internshipzappa.R.id.btnPlus)
            btn_minus = itemView.findViewById(com.zlobrynya.internshipzappa.R.id.btnMinus)
            tv_counter = itemView.findViewById(com.zlobrynya.internshipzappa.R.id.tvCounter)

            btn_toping?.setOnClickListener {
                btn_toping?.visibility = View.GONE
                btn_plus?.visibility = View.VISIBLE
                btn_minus?.visibility = View.VISIBLE
                tv_counter?.visibility = View.VISIBLE
            }

            btn_plus?.setOnClickListener{
                tv_counter!!.text = (tv_counter!!.text.toString().toInt() + 1).toString()
                if(tv_counter!!.text == "15"){
                    btn_plus!!.visibility = View.GONE
                }
            }

            btn_minus?.setOnClickListener{
                tv_counter?.text = (tv_counter!!.text.toString().toInt() - 1).toString()
                if (tv_counter?.text == "0") {
                    btn_toping?.visibility = View.VISIBLE
                    btn_plus?.visibility = View.GONE
                    btn_minus?.visibility = View.GONE
                    tv_counter?.visibility = View.GONE
                    tv_counter?.text = "1"
                    Toast.makeText(itemView.context, "Топинг убран из заказа", Toast.LENGTH_SHORT).show()
                } else if (tv_counter!!.text == "14"){
                    btn_plus!!.visibility = View.VISIBLE
                }
            }
        }
    }
}