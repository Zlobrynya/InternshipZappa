package com.zlobrynya.internshipzappa.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zlobrynya.internshipzappa.R

<<<<<<< HEAD
=======

>>>>>>> 5b7d7e25e5fcf99fad4402f34217178e50f67031
/**
 * Фрагмент-контейнер для фрагментов связанных с бронью
 */
class RootFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_root, container, false)

        val transaction = fragmentManager!!.beginTransaction()
        // Загружаем контейнер для фрагментов "новая бронь" и "мои брони"
        transaction.replace(R.id.root_frame, BookingContainerFragment())
        transaction.commit()

        return view
    }

<<<<<<< HEAD

=======
>>>>>>> 5b7d7e25e5fcf99fad4402f34217178e50f67031
}
