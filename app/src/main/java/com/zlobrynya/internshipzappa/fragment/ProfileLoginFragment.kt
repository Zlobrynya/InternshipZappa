package com.zlobrynya.internshipzappa.fragment

import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.zlobrynya.internshipzappa.R
import com.zlobrynya.internshipzappa.activity.profile.LoginActivity
import kotlinx.android.synthetic.main.fragment_profile_login.view.*

/**
 * Фрагмент для вкладки "Профиль" когда пользователь не авторизован
 */
class ProfileLoginFragment : Fragment() {

    var lastCLickTime: Long = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_profile_login, container, false)
        view.login_button.setOnClickListener(onClickListener)
        return view
    }

    /**
     * Обработчик нажатий
     */
    private val onClickListener = View.OnClickListener {
        when (it.id) {
            // Кнопка залогиниться
            R.id.login_button -> {
                if (SystemClock.elapsedRealtime() - lastCLickTime < 1000) {
                    return@OnClickListener
                } else {
                    lastCLickTime = SystemClock.elapsedRealtime()
                    openLoginActivity()
                }
            }
        }
    }

    /**
     * Открывает логин активити
     */
    private fun openLoginActivity() {
        val intent = Intent(context, LoginActivity::class.java)
        startActivity(intent)
    }

}
