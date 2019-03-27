package com.zlobrynya.internshipzappa.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.support.v7.app.ActionBar
import android.util.Log
import android.view.MenuItem
import com.zlobrynya.internshipzappa.R
import com.zlobrynya.internshipzappa.fragment.KontaktiFragment
import com.zlobrynya.internshipzappa.fragment.ProfileFragment
import com.zlobrynya.internshipzappa.fragment.menu.MenuFragment
import kotlinx.android.synthetic.main.activity_menu2.*
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import com.zlobrynya.internshipzappa.fragment.*

const val MENU_PAGE: Int = 0
const val BOOKING_PAGE: Int = 1
const val CONTACTS_PAGE: Int = 2
const val PROFILE_PAGE: Int = 3

const val REQUEST_CODE: Int = 11

class Menu2Activity : AppCompatActivity() {

    private val menuFragment: MenuFragment = MenuFragment()
    private val contactsFragment: KontaktiFragment = KontaktiFragment()
    private val rootFragment: RootFragment = RootFragment()
    private val rootFragment2: RootFragment2 = RootFragment2()
    private val profileFragment: ProfileFragment = ProfileFragment()

    internal var prevMenuItem: MenuItem? = null
    private var toolbar: ActionBar? = null

    private var mPagerAdapter: SlidePagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu2)
        hideToolbar()
        initNavigation()
        initAdapter()
    }

    /**
     * Инициалирует навигацию по вкладкам
     */
    private fun initNavigation() {
        navigation2.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_menu -> {
                    viewpager2.currentItem = MENU_PAGE
                }
                R.id.navigation_booking -> {
                    viewpager2.currentItem = BOOKING_PAGE
                }
                R.id.navigation_profile -> {
                    viewpager2.currentItem = PROFILE_PAGE
                }
            }
            false
        }

        viewpager2.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                if (prevMenuItem != null) {
                    prevMenuItem!!.isChecked = false
                } else {
                    navigation2.menu.getItem(0).isChecked = false
                }
                Log.d("page", "onPageSelected: $position")
                navigation2.menu.getItem(position).isChecked = true
                prevMenuItem = navigation2.menu.getItem(position)

            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })

        viewpager2.offscreenPageLimit = 3 // Установим отступ для кеширования страниц
    }

    /**
     * Инициализирует адаптер
     */
    private fun initAdapter() {
        mPagerAdapter = SlidePagerAdapter(supportFragmentManager)
        viewpager2.adapter = mPagerAdapter
    }

    /**
     * Скрывает тулбар
     */
    private fun hideToolbar() {
        toolbar = supportActionBar
        toolbar!!.hide()
    }

    /**
     * Кастомный адаптер для постраничной навигации
     */
    inner class SlidePagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {

            return when (position) {
                MENU_PAGE -> menuFragment
                BOOKING_PAGE -> rootFragment // На вкладку "Бронь" положим фрагмент-контейнер
                else -> rootFragment2
            }
        }

        override fun getCount(): Int {
            return 3
        }
    }

}
