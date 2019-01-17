package com.zlobrynya.internshipzappa.tools.parcelable

import android.os.Parcel
import android.os.Parcelable
import com.zlobrynya.internshipzappa.tools.json.Dish

class CategoryParcelable() : Parcelable {
    constructor(parcel: Parcel) : this() {
        parcel.readList(listMenu, null)
    }
    lateinit var listMenu: ArrayList<Dish>

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeList(listMenu)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CategoryParcelable> {
        override fun createFromParcel(parcel: Parcel): CategoryParcelable {
            return CategoryParcelable(parcel)
        }

        override fun newArray(size: Int): Array<CategoryParcelable?> {
            return arrayOfNulls(size)
        }
    }
}