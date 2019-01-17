package com.zlobrynya.internshipzappa.adapter

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.zlobrynya.internshipzappa.R
import com.zlobrynya.internshipzappa.tools.json.Dish
import android.widget.ImageView



class AdapterListCategory(context: Context?, resource: Int) :
    ArrayAdapter<Dish>(context, resource) {

    class Holder{
        var nameDish: TextView? = null
        var imageView: ImageView? = null
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = convertView
        var holder = Holder()

        if (view != null){
            holder = view.tag as Holder
        }else{
            val vi = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = vi.inflate(R.layout.item_category_menu, null)
            holder.nameDish = view.findViewById(R.id.nameDish)
            holder.imageView = view.findViewById(R.id.imageView)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                holder.imageView!!.clipToOutline = true
            }
            view.tag = holder
        }

        val dish = getItem(position)
        holder.nameDish!!.text = dish?.name
        return view!!
    }
}

