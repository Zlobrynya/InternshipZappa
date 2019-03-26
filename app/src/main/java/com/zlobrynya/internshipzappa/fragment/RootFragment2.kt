package com.zlobrynya.internshipzappa.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zlobrynya.internshipzappa.R

/**
 * Фрагмент-контейнер для фрагментов связанных с профилем
 */
class RootFragment2 : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_root_2, container, false)

        val transaction = fragmentManager!!.beginTransaction()
        transaction.replace(R.id.root_frame2, ProfileFragment())
        transaction.commit()

        return view
    }
}
