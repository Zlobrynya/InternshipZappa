package com.zlobrynya.internshipzappa.activity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.support.v7.app.ActionBar
import android.util.Log
import android.view.MenuItem
import android.view.View
import com.zlobrynya.internshipzappa.R
import com.zlobrynya.internshipzappa.activity.profile.CodeFEmailActivity
import com.zlobrynya.internshipzappa.fragment.BookingFragment
import com.zlobrynya.internshipzappa.fragment.KontaktiFragment
import com.zlobrynya.internshipzappa.fragment.menu.MenuFragment
import kotlinx.android.synthetic.main.activity_menu2.*

class Menu2Activity : AppCompatActivity() {

    private lateinit var menuFragment: MenuFragment
    private lateinit var bookingFragment: BookingFragment
    private lateinit var contactsFragment: KontaktiFragment
    private lateinit var codeFEmailActivity: CodeFEmailActivity
    internal var prevMenuItem: MenuItem? = null
    private var toolbar: ActionBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu2)
        toolbar = supportActionBar
        toolbar!!.hide()

        navigation2.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_menu -> {
                    viewpager2.currentItem = 0
                }
                R.id.navigation_booking -> {
                    viewpager2.currentItem = 1
                }
                R.id.navigation_contacts -> {
                    //viewpager2.currentItem = 2
                    val intent = Intent(this, CodeFEmailActivity::class.java)
                    startActivity(intent)
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

        viewpager2.offscreenPageLimit = 2

        setupViewPager(viewpager2)
    }

    private fun setupViewPager(viewPager: ViewPager) {
        val adapter = ViewPagerAdapter(supportFragmentManager)
        bookingFragment = BookingFragment()
        contactsFragment = KontaktiFragment()
        menuFragment = MenuFragment()
        codeFEmailActivity = CodeFEmailActivity()

        adapter.addFragment(menuFragment)
        adapter.addFragment(bookingFragment)
        adapter.addFragment(contactsFragment)
        viewPager.adapter = adapter
    }
}
