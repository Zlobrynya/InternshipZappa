package com.zlobrynya.internshipzappa.fragment


import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zlobrynya.internshipzappa.R
import com.zlobrynya.internshipzappa.activity.profile.LoginActivity

import kotlinx.android.synthetic.main.fragment_profile.*

/**
 * Фрагмент профиля.
 *
 */
class ProfileFragment : Fragment() {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        btnLinkToLoginActivity.setOnClickListener {
            val i = Intent(activity, LoginActivity::class.java)
            startActivity(i)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

}
