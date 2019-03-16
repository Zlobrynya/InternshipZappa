package com.zlobrynya.internshipzappa.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.support.v7.app.ActionBar
import android.util.Log
import android.view.MenuItem
import com.zlobrynya.internshipzappa.R
import com.zlobrynya.internshipzappa.fragment.BookingFragment
import com.zlobrynya.internshipzappa.fragment.KontaktiFragment
import com.zlobrynya.internshipzappa.fragment.menu.MenuFragment
import kotlinx.android.synthetic.main.activity_menu2.*

class Menu2Activity : AppCompatActivity() {

    private lateinit var menuFragment: MenuFragment
    private lateinit var bookingFragment: BookingFragment
    private lateinit var contactsFragment: KontaktiFragment
    internal var prevMenuItem: MenuItem? = null
    private var toolbar: ActionBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu2)
        toolbar = supportActionBar

        if (savedInstanceState == null) {
            toolbar!!.title = "Меню"
        }

        navigation2.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_menu -> {
                    toolbar!!.title = "Меню"
                    viewpager2.currentItem = 0
                }
                R.id.navigation_booking -> {
                    toolbar!!.title = "Бронь"
                    viewpager2.currentItem = 1
                }
                R.id.navigation_contacts -> {
                    toolbar!!.title = "Контакты"
                    viewpager2.currentItem = 2
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

        //Disable ViewPager Swipe

        /*viewPager.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                return true;
            }
        });*/

        setupViewPager(viewpager2)
    }

    private fun setupViewPager(viewPager: ViewPager) {
        val adapter = ViewPagerAdapter(supportFragmentManager)
        bookingFragment = BookingFragment()
        contactsFragment = KontaktiFragment()
        menuFragment = MenuFragment()

        adapter.addFragment(menuFragment)
        adapter.addFragment(bookingFragment)
        adapter.addFragment(contactsFragment)
        viewPager.adapter = adapter
    }
}
