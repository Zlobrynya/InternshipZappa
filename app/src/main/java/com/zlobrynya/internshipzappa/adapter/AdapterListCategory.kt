package com.zlobrynya.internshipzappa.adapter

import android.content.Context
import android.os.Build
import android.support.v7.widget.CardView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.zlobrynya.internshipzappa.R
import com.zlobrynya.internshipzappa.tools.json.Dish


class AdapterListCategory(context: Context?, resource: Int) :
    ArrayAdapter<Dish>(context, resource) {

    class Holder{
        var buttonDish: Button? = null
        var imageView: Button? = null
        var descDish: TextView? = null
        var priceDish: TextView? = null
        var weightDish: TextView? = null
        var shapeDish: CardView? = null

    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = convertView
        var holder = Holder()

        if (view != null){
            holder = view.tag as Holder
        }else{
            val vi = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = vi.inflate(R.layout.item_category_menu, null)
            holder.buttonDish = view.findViewById(R.id.dishButton)
            holder.imageView = view.findViewById(R.id.dishButton)
            holder.descDish = view.findViewById(R.id.descDish)
            holder.priceDish = view.findViewById(R.id.priceDish)
            holder.weightDish = view.findViewById(R.id.weightDish)
            holder.shapeDish = view.findViewById(R.id.shapeDish)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                holder.imageView!!.clipToOutline = true
            }
            view.tag = holder
        }

        val dish = getItem(position)
        holder.buttonDish!!.text = dish?.name
        holder.descDish!!.text = dish?.descr
        holder.weightDish!!.text = dish?.weight
        holder.priceDish!!.text = dish?.price.toString() + " руб."
        holder.buttonDish!!.setOnClickListener {
            if(holder.shapeDish!!.visibility == View.VISIBLE) holder.shapeDish!!.visibility = View.GONE
            else
            holder.shapeDish!!.visibility = View.VISIBLE
        }
        return view!!
    }
}

