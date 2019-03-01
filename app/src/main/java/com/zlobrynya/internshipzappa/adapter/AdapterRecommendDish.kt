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
import kotlinx.android.synthetic.main.item_rect_recommend_dish.view.*


/*
* Адаптер для RecyclerView рекомендованных блюд на экране FullDescriptionScreen
* */
class AdapterRecommendDish(private val values: ArrayList<DishDTO>): RecyclerView.Adapter<AdapterRecommendDish.ViewHolder>() {

    override fun getItemCount() = values.size

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_rect_recommend_dish, parent, false)

        return ViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(values.get(position))
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView), View.OnClickListener{
        var imageLoader: ImageLoader

        init {
            imageLoader = ImageLoader.getInstance()
        }
        // Установка данных в view
        @SuppressLint("SetTextI18n")
        fun bind(dishDTO: DishDTO) = with(itemView){
            topingPrice.text = if (dishDTO.price == 0.0) "-"
            else dishDTO.price.toInt().toString() + context.getString(R.string.rub)

            if (dishDTO.weight == 0){
                topingVes.visibility = View.GONE
            } else{
                topingVes.text = dishDTO.weight.toString() + context.getString(R.string.gr)
            }

            topingName.text = dishDTO.name

            imageLoader.displayImage(dishDTO.photo, topingPhoto, object: ImageLoadingListener{
                override fun onLoadingComplete(imageUri: String?, view: View?, loadedImage: Bitmap?) {
                    progressBar2.visibility = View.GONE
                }

                override fun onLoadingStarted(imageUri: String?, view: View?) {
                    progressBar2.visibility = View.VISIBLE
                }

                override fun onLoadingCancelled(imageUri: String?, view: View?) {
                    progressBar2.visibility = View.GONE
                    imageLoader.displayImage("drawable://"+ R.drawable.no_toping, topingPhoto)
                }

                override fun onLoadingFailed(imageUri: String?, view: View?, failReason: FailReason?) {
                    progressBar2.visibility = View.GONE
                    imageLoader.displayImage("drawable://"+ R.drawable.no_toping, topingPhoto)
                }
            })

            /*btnToping.setOnClickListener(this@ViewHolder)
            btnPlus.setOnClickListener(this@ViewHolder)
            btnMinus.setOnClickListener(this@ViewHolder)*/
        }

        override fun onClick(view: View) = with(itemView) {
            when (view){
                btnToping -> {
                    btnToping.visibility = View.GONE
                    btnPlus.visibility = View.VISIBLE
                    btnMinus.visibility = View.VISIBLE
                    tvCounter.visibility = View.VISIBLE
                }
                btnPlus -> {
                    tvCounter.text = (tvCounter.text.toString().toInt() + 1).toString()
                    if (tvCounter.text == context.getString(R.string.max_order))
                        btnPlus.visibility = View.GONE
                }
                btnMinus -> {
                    tvCounter.text = (tvCounter.text.toString().toInt() - 1).toString()
                    when (tvCounter.text){
                        context.getString(R.string.zero_num) ->{
                            btnToping.visibility = View.VISIBLE
                            btnPlus.visibility = View.GONE
                            btnMinus.visibility = View.GONE
                            tvCounter.visibility = View.GONE
                            tvCounter.text = context.getString(R.string.default_num)
                            Toast.makeText(itemView.context, context.getString(R.string.topping_delete), Toast.LENGTH_SHORT).show()
                        }
                        (context.getString(R.string.max_order).toInt() - 1).toString() ->{
                            btnPlus.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }
    }
}