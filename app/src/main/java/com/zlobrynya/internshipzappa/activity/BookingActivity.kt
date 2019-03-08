package com.zlobrynya.internshipzappa.activity

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import com.zlobrynya.internshipzappa.R
import com.zlobrynya.internshipzappa.fragment.BookingFragment
import kotlinx.android.synthetic.main.activity_booking.*

/**
 * Активити для бронирования с нижней навигационной панелью по фрагментам
 */
class BookingActivity : AppCompatActivity() {

    /**
     * Экземпляр фрагмента брони(выбор даты и времени)
     */
    private val bookingFragment: Fragment = BookingFragment()

    /**
     * Обработчик нажатий на нижнюю навигационную панель
     */
    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        var selectedFragment: Fragment? = null
        when (item.itemId) {
            // Меню
            R.id.navigation_menu -> {
                //Какой то код
            }
            // Профиль
            R.id.navigation_profile -> {
                //Какой то код
            }
            // Бронирование
            R.id.navigation_booking -> {
                selectedFragment = bookingFragment
            }
            // Корзина
            R.id.navigation_basket -> {
                //Какой то код
            }
        }
        // Загружаем фрагмент
        if (selectedFragment != null) {
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container, selectedFragment).commit()
        }
        true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking)

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }
}
