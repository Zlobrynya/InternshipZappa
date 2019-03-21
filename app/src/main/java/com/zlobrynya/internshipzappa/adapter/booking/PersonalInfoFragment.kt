package com.zlobrynya.internshipzappa.adapter.booking


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.zlobrynya.internshipzappa.R
import com.zlobrynya.internshipzappa.activity.profile.LoginActivity
import kotlinx.android.synthetic.main.fragment_personal_info.view.*

const val REQUEST_CODE: Int = 11

/**
 * Фрагмент персональное инфо
 * Калька с одноименной активити
 * TODO доделать копипаст функционала исходной активити
 */
class PersonalInfoFragment : Fragment() {

    /**
     * Обработчик нажатий на стрелочку в тулбаре
     */
    private val navigationClickListener = View.OnClickListener {
        closeFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        var view = inflater.inflate(R.layout.fragment_personal_info, container, false)
        view = initToolBar(view)

        // TODO активти не должна запускаться, если юзер уже авторизован
        openLoginActivity()

        // Чтобы принять аргумент во фрагменте пиши arguments!!.getString или getInt

        return view
    }

    /**
     * Настраивает тулбар
     */
    private fun initToolBar(view: View): View {
        view.enter_personal_info.setNavigationIcon(R.drawable.ic_back_button) // Установим иконку в тулбаре
        view.enter_personal_info.setNavigationOnClickListener(navigationClickListener) // Установим обработчик нажатий на тулбар
        return view
    }

    /**
     * Открывает логин активити
     */
    private fun openLoginActivity() {
        val intent = Intent(activity, LoginActivity::class.java)
        startActivityForResult(intent, REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                // Логин активити успешно завершила работу
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // Юзер нажал назад
                closeFragment()
            }
        }
    }

    /**
     * Закрывает текущий фрагмент и удаляет его со стека
     */
    private fun closeFragment() {
        val trans = fragmentManager!!.beginTransaction()
        trans.remove(this)
        trans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        trans.commit()
        fragmentManager!!.popBackStack()
    }
}
