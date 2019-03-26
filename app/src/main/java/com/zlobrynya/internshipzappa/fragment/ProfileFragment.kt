package com.zlobrynya.internshipzappa.fragment


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zlobrynya.internshipzappa.R

import kotlinx.android.synthetic.main.fragment_profile.view.*

/**
 * Фрагмент профиля.
 *
 */
class ProfileFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        var view = inflater.inflate(R.layout.fragment_profile, container, false)

        view.btnEdit.setOnClickListener {
            val trans = fragmentManager!!.beginTransaction()
            val editProfileFragment = EditProfileFragment()
            trans.add(R.id.root_frame2, editProfileFragment)
            trans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            trans.addToBackStack(null)
            trans.commit()
        }

        return view
    }

    fun String.toEditable(): Editable =  Editable.Factory.getInstance().newEditable(this)
}