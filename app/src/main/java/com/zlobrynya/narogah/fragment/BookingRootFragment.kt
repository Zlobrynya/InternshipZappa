package com.zlobrynya.narogah.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zlobrynya.narogah.R

/**
 * Фрагмент-контейнер для фрагментов связанных с бронью
 */
class BookingRootFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_booking_root, container, false)

        val transaction = fragmentManager!!.beginTransaction()
        // Загружаем контейнер для фрагментов "новая бронь" и "мои брони"
        transaction.replace(R.id.root_frame, BookingContainerFragment())
        transaction.commit()

        return view
    }
}
